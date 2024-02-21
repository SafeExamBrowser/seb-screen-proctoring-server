/*
 * Copyright (c) 2019 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.server.datalayer.dao.WebserviceInfoDAO;
import ch.ethz.seb.sps.utils.Constants;

@Lazy
@Service
public class ServiceInfo {

    private static final Logger log = LoggerFactory.getLogger(ServiceInfo.class);

    private static final String WEB_SERVICE_HOST_ADDRESS_KEY = "server.address";
    private static final String WEB_SERVICE_SERVER_PORT_KEY = "server.port";

    private static final String WEB_SERVICE_SERVER_NAME_KEY = "sps.webservice.http.external.servername";
    private static final String WEB_SERVICE_HTTP_SCHEME_KEY = "sps.webservice.http.external.scheme";
    private static final String WEB_SERVICE_HTTP_PORT = "sps.webservice.http.external.port";
    private static final String WEB_SERVICE_CONTEXT_PATH = "server.servlet.context-path";
    private static final String GUI_REDIRECT_URL = "sps.gui.redirect.url";

    public static final String VERSION_KEY = "seb.sps.version";
    public static final String STORE_ADAPTER_KEY = "sps.data.store.adapter";
    public static final String STORE_STRATEGY_KEY = "sps.data.store.strategy";
    public static final String FILE_STORE = "FILESYS_RDBMS";
    public static final String FULL_RDBMS_STORE = "FULL_RDBMS";
    public static final String S3_STORE = "S3";


    private final String version;
    private final Set<String> activeProfiles;
    private final String storeAdapter;
    private final String storeStrategy;

    private final String hostAddress; // internal
    private final String serverPort; // internal
    private final String contextPath;

    private final String adminAPIEndpoint;
    private final String adminAPIEndpointV1;
    private final String sessionAPIEndpoint;
    private final String sessionAPIEndpointV1;
    private final int adminAccessTokenValSec;
    private final int adminRefreshTokenValSec;
    private final int sessionAPITokenValiditySeconds;

    private final WebserviceInfoDAO webserviceInfoDAO;

    private final boolean isDistributed;
    private final long distributedUpdateInterval;
    private final String webserviceUUID;
    private boolean isMaster = false;

    private final boolean isSEBServerBundle;

    private final String httpScheme; // external
    private final String webserverName; // external
    private final String webserverPort; // external
    private final String externalServiceURI;
    private final String guiRedirectURL;
    private final String screenshotRequestURI;

    public ServiceInfo(
            final Environment environment,
            final WebserviceInfoDAO webserviceInfoDAO) {

        this.version = environment.getRequiredProperty(VERSION_KEY);
        this.activeProfiles = new HashSet<>(Arrays.asList(environment.getActiveProfiles()));
        this.storeAdapter = environment.getRequiredProperty(STORE_ADAPTER_KEY);
        this.storeStrategy = environment.getRequiredProperty(STORE_STRATEGY_KEY);
        this.webserviceInfoDAO = webserviceInfoDAO;

        this.adminAPIEndpoint = environment.getProperty("sps.api.admin.endpoint");
        this.adminAPIEndpointV1 = environment.getProperty("sps.api.admin.endpoint.v1");
        this.sessionAPIEndpoint = environment.getProperty("sps.api.session.endpoint");
        this.sessionAPIEndpointV1 = environment.getProperty("sps.api.session.endpoint.v1");

        this.adminAccessTokenValSec = environment.getProperty(
                "sps.api.admin.accessTokenValiditySeconds",
                Integer.class,
                3600);
        this.adminRefreshTokenValSec = environment.getProperty(
                "sps.api.admin.refreshTokenValiditySeconds",
                Integer.class,
                -1);
        this.sessionAPITokenValiditySeconds = environment.getProperty(
                "sps.api.session.accessTokenValiditySeconds",
                Integer.class,
                43200);

        // internal
        this.hostAddress = environment.getRequiredProperty(WEB_SERVICE_HOST_ADDRESS_KEY);
        this.serverPort = environment.getRequiredProperty(WEB_SERVICE_SERVER_PORT_KEY);
        this.contextPath = environment.getProperty(WEB_SERVICE_CONTEXT_PATH, "");
        // external
        this.httpScheme = environment.getRequiredProperty(WEB_SERVICE_HTTP_SCHEME_KEY);
        this.webserverPort = environment.getProperty(WEB_SERVICE_HTTP_PORT);
        this.webserverName = environment.getProperty(WEB_SERVICE_SERVER_NAME_KEY, "");
        if (StringUtils.isEmpty(this.webserverName)) {
            log.error("NOTE: External server name, property : 'sps.webservice.http.external.servername' is not set!");
            throw new IllegalArgumentException(
                    "External server name, property : 'sps.webservice.http.external.servername' is not set!");
        } else if (this.webserverName.contains("localhost")) {
            log.warn("NOTE: External server name, property : 'sps.webservice.http.external.servername' "
                    + "is set to localhost. This is only for local development setups.");
        }
        this.guiRedirectURL = environment.getRequiredProperty(GUI_REDIRECT_URL);

        final UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme(this.httpScheme)
                .host((StringUtils.isNotBlank(this.webserverName))
                        ? this.webserverName
                        : this.hostAddress);
        if (StringUtils.isNotBlank(this.webserverPort)) {
            builder.port(this.webserverPort);
        }
        if (StringUtils.isNotBlank(this.contextPath) && !this.contextPath.equals("/")) {
            builder.path(this.contextPath);
        }
        this.externalServiceURI = builder.toUriString();
        this.screenshotRequestURI =
                this.externalServiceURI + this.adminAPIEndpointV1 +
                        API.PROCTORING_ENDPOINT +
                        API.SCREENSHOT_ENDPOINT;

        this.webserviceUUID = UUID.randomUUID().toString()
                + Constants.UNDERLINE
                + this.version;

        this.isDistributed = BooleanUtils.toBoolean(environment.getProperty(
                "sps.webservice.distributed",
                Constants.FALSE_STRING));

        this.distributedUpdateInterval = environment.getProperty(
                "sps.webservice.distributed.updateInterval",
                Long.class,
                2000L);

        this.isSEBServerBundle = BooleanUtils.toBoolean(environment.getProperty(
                "sps.webservice.sebserver.bundle",
                Constants.TRUE_STRING));
    }

    public boolean isMaster() {
        return this.isMaster;
    }

    boolean registerWebservice() {
        return this.webserviceInfoDAO.register(this.webserviceUUID, getLocalHostAddress());
    }

    void unregister() {
        if (isWebserviceInitialized()) {
            this.webserviceInfoDAO.unregister(this.webserviceUUID);
        }
    }

    boolean isWebserviceInitialized() {
        return this.webserviceInfoDAO.isInitialized();
    }

    public void updateMaster() {
        this.isMaster = this.webserviceInfoDAO.isMaster(this.getWebserviceUUID());
    }

    public boolean isDistributed() {
        return this.isDistributed;
    }

    public boolean isSEBServerBundle() {
        return this.isSEBServerBundle;
    }

    public long getDistributedUpdateInterval() {
        return this.distributedUpdateInterval;
    }

    public String getWebserviceUUID() {
        return this.webserviceUUID;
    }

    public String getVersion() {
        return this.version;
    }

    public boolean hasProfile(final String profile) {
        return this.activeProfiles.contains(profile);
    }

    public int getAdminAccessTokenValSec() {
        return this.adminAccessTokenValSec;
    }

    public int getAdminRefreshTokenValSec() {
        return this.adminRefreshTokenValSec;
    }

    public int getSessionAPITokenValiditySeconds() {
        return this.sessionAPITokenValiditySeconds;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    public String getHttpScheme() {
        return this.httpScheme;
    }

    public String getWebserverName() {
        return this.webserverName;
    }

    public String getWebserverPort() {
        return this.webserverPort;
    }

    public String getExternalServiceURI() {
        return this.externalServiceURI;
    }

    public String getGuiRedirectURL() {
        return this.guiRedirectURL;
    }

    public void setMaster(final boolean isMaster) {
        this.isMaster = isMaster;
    }

    public String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (final UnknownHostException e) {
            log.error("Failed to get local host name: {}", e.getMessage());
            return Constants.EMPTY_NOTE;
        }
    }

    public String getLocalHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (final UnknownHostException e) {
            log.error("Failed to get local host address: {}", e.getMessage());
            return Constants.EMPTY_NOTE;
        }
    }

    public String getLoopbackHostName() {
        return InetAddress.getLoopbackAddress().getHostName();
    }

    public String getLoopbackHostAddress() {
        return InetAddress.getLoopbackAddress().getHostAddress();
    }

    public Set<String> getActiveProfiles() {
        return this.activeProfiles;
    }

    public String getStoreAdapter() {
        return this.storeAdapter;
    }

    public String getStoreStrategy() {
        return this.storeStrategy;
    }

    public boolean isFileStore() {
        return FILE_STORE.equals(this.storeAdapter);
    }

    public boolean isFullRDBMSStore() {
        return FULL_RDBMS_STORE.equals(this.storeAdapter);
    }

    public boolean isS3Store() {
        return S3_STORE.equals(this.storeAdapter);
    }

    public String getHostAddress() {
        return this.hostAddress;
    }

    public String getServerPort() {
        return this.serverPort;
    }

    public String getAdminAPIEndpoint() {
        return this.adminAPIEndpoint;
    }

    public String getAdminAPIEndpointV1() {
        return this.adminAPIEndpointV1;
    }

    public String getSessionAPIEndpoint() {
        return this.sessionAPIEndpoint;
    }

    public String getSessionAPIEndpointV1() {
        return this.sessionAPIEndpointV1;
    }

    public String getScreenshotRequestURI() {
        return this.screenshotRequestURI;
    }

}
