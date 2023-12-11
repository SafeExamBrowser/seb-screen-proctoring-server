/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.bot;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.http.OAuth2ErrorHandler;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HTTPClientBot {

    private static final Logger log = LoggerFactory.getLogger(HTTPClientBot.class);

    private final Random random = new Random();
    private final ExecutorService executorService;
    private Profile profile;
    private final ObjectMapper jsonMapper = new ObjectMapper();

    private final AtomicLong screenshots = new AtomicLong();

    public HTTPClientBot(final Properties properties) throws Exception {

        final String profileName = properties.getProperty("profile");
        if (StringUtils.isNotBlank(profileName)) {
            try {
                final ResourceLoader resourceLoader = new DefaultResourceLoader();
                final Resource resource = resourceLoader.getResource("classpath:/profiles/" + profileName + ".json");
                this.profile = this.jsonMapper.readValue(resource.getInputStream(), Profile.class);
            } catch (final Exception e) {
                log.error("Failed to load profile: {}", profileName, e);
                this.profile = new Profile();
                throw e;
            }
        } else {
            this.profile = new Profile();
        }

        this.executorService = Executors.newFixedThreadPool(this.profile.numberOfConnections);
        for (int i = 0; i < this.profile.numberOfConnections; i++) {
            final String sessionId = StringUtils.isNotBlank(this.profile.sessionId)
                    ? this.profile.sessionId
                    : "seb_" + getRandomName() + "_" + i;

            this.executorService.execute(new ConnectionBot(sessionId));

            try {
                Thread.sleep(this.profile.spawnDelay);
            } catch (final Exception e) {

            }
        }

        this.executorService.shutdown();

        final boolean probe = true;
        if (probe) {
            while (true) {
                this.screenshots.set(0);
                Thread.sleep(1000);
                System.out.println("********* screenshots per second: " + this.screenshots.get());
            }
        }
    }

    private final class ConnectionBot implements Runnable {

        private final String name;
        private final OAuth2RestTemplate restTemplate;

        private final String createSessionURL = HTTPClientBot.this.profile.webserviceAddress +
                HTTPClientBot.this.profile.apiPath +
                API.SESSION_ENDPOINT;

        private final String closeSessionURL = HTTPClientBot.this.profile.webserviceAddress +
                HTTPClientBot.this.profile.apiPath +
                API.SESSION_ENDPOINT +
                API.PARAM_MODEL_PATH_SEGMENT;

        private final String imageUploadURI = HTTPClientBot.this.profile.webserviceAddress +
                HTTPClientBot.this.profile.apiPath +
                API.SESSION_ENDPOINT +
                API.PARAM_MODEL_PATH_SEGMENT +
                API.SCREENSHOT_ENDPOINT;

        public ConnectionBot(final String name) {
            this.name = name;
            this.restTemplate = createRestTemplate(null);
        }

        @Override
        public void run() {
            log.info("ConnectionBot {} : SEB-Connection-Bot started: {}", HTTPClientBot.this.profile);

            String sessionUUID = null;

            try {

                final OAuth2AccessToken accessToken = this.restTemplate.getAccessToken();
                log.info("ConnectionBot {} : Got access token: {}", this.name, accessToken);
                sessionUUID = createSession();

                if (sessionUUID == null) {
                    throw new RuntimeException("Handshake failed, no session has been generated!");
                }

                log.info("Successfully initialized session: {}", sessionUUID);

            } catch (final Exception e) {
                log.error("ConnectionBot {} : Failed : ", this.name, e);
            }

            if (sessionUUID != null) {
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + HTTPClientBot.this.profile.runtime;
                long currentTime = startTime;
                long lastScreenshotTime = startTime;

                long totalDuration = endTime - startTime;
                long oneThirdTime = startTime + totalDuration / 3;
                long twoThirdTime = startTime + 2 * totalDuration / 3;

                while (currentTime < endTime) {
                    if (currentTime - lastScreenshotTime >= HTTPClientBot.this.profile.screenshotInterval) {

                        if (currentTime < oneThirdTime) {
                            screenshot(sessionUUID, 0);
                        } else if (currentTime >= oneThirdTime && currentTime < twoThirdTime) {
                            screenshot(sessionUUID, 1);
                        } else {
                            screenshot(sessionUUID, 2);
                        }

                        lastScreenshotTime = currentTime;
                    }

                    try {
                        Thread.sleep(20);
                    } catch (final Exception e) {
                    }
                    currentTime = System.currentTimeMillis();
                }

                closeSession(sessionUUID);
            }
        }

        private void screenshot(final String sessionUUID,int index) {

            if (log.isTraceEnabled()) {
                log.debug("ConnectionBot {} : take screenshot...", this.name);
            }

            HTTPClientBot.this.screenshots.incrementAndGet();

            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            takeScreenshot(byteArrayOutputStream);

            final String metaData = createMetaData(index);

            final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            headers.set(Domain.SCREENSHOT_DATA.ATTR_TIMESTAMP, String.valueOf(Utils.getMillisecondsNow()));
            headers.set(Domain.SCREENSHOT_DATA.ATTR_IMAGE_FORMAT, "jpg");
            if (metaData != null) {
                headers.set(Domain.SCREENSHOT_DATA.ATTR_META_DATA, metaData);
            }

            final HttpEntity<byte[]> entity = new HttpEntity<>(byteArrayOutputStream.toByteArray(), headers);

            if (log.isTraceEnabled()) {
                log.debug("ConnectionBot {} : send screenshot...", this.name);
            }

            final ResponseEntity<Void> exchange = this.restTemplate.exchange(
                    this.imageUploadURI,
                    HttpMethod.POST,
                    entity,
                    Void.class,
                    sessionUUID);

            if (exchange.getStatusCode() != HttpStatus.OK) {
                log.error("Failed to send screenshot: {}", exchange.getStatusCode());
            } else {
                try {
                    final String health = exchange.getHeaders().getFirst("sps_server_health");
                    if (health != null && Integer.parseInt(health) > 0) {
                        System.out.println("********** health: " + health);
                    }
                } catch (final Exception e) {
                }
            }
        }

        private final List<String> urls = Stream.of(
                "https://google.com",
                "https://zoom.com",
                "https://chat.com",
                "https://moodle.com",
                "https://safeexambrowser.org"
        )
        .collect(Collectors.toList());
        private final List<String> titles = Stream.of(
                "Safe Exam Browser.Client",
                "Web-Browser",
                "Safe Exam Browser.Client"
        )
        .collect(Collectors.toList());
        private final List<String> actions = Stream.of(
                "Moodle Page 1",
                "Moodle Page 2",
                "Moodle Page 3"
        )
        .collect(Collectors.toList());

        private String createMetaData(int index) {
            final Map<String, String> metadata = new HashMap<>();
                metadata.put(
                        API.SCREENSHOT_META_DATA_BROWSER_URL,
                        this.urls.get(HTTPClientBot.this.random.nextInt(this.urls.size())));

                metadata.put(
                        API.SCREENSHOT_META_DATA_ACTIVE_WINDOW_TITLE,
                        this.titles.get(index));
                
                metadata.put(
                    API.SCREENSHOT_META_DATA_USER_ACTION,
                    this.actions.get(HTTPClientBot.this.random.nextInt(this.actions.size())));

            try {
                return HTTPClientBot.this.jsonMapper.writeValueAsString(metadata);
            } catch (final JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }

        private String createSession() {
            log.info("ConnectionBot {} : init connection", this.name);

            final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            headers.set(API.GROUP_HEADER_UUID, HTTPClientBot.this.profile.groupId);
            headers.set(API.SESSION_HEADER_SEB_USER_NAME, this.name);
            headers.set(API.SESSION_HEADER_SEB_IP, "0.0.0.0");
            headers.set(API.SESSION_HEADER_SEB_MACHINE_NAME, "localhost");
            headers.set(API.SESSION_HEADER_SEB_OS, "windows");
            headers.set(API.SESSION_HEADER_SEB_VERSION, "0.1-httpBot");

            final ResponseEntity<Void> exchange = this.restTemplate.exchange(
                    this.createSessionURL,
                    HttpMethod.POST,
                    new HttpEntity<>(headers),
                    Void.class);

            if (exchange.getStatusCode() != HttpStatus.OK) {
                log.error("Handshake failed: {}", exchange.getStatusCode());
                return null;
            }

            return exchange.getHeaders().getFirst(API.SESSION_HEADER_UUID);
        }

        private void closeSession(final String sessionUUID) {
            log.info("ConnectionBot {} : close session {}", this.name, sessionUUID);

            final ResponseEntity<Void> exchange = this.restTemplate.exchange(
                    this.closeSessionURL,
                    HttpMethod.DELETE,
                    HttpEntity.EMPTY,
                    Void.class,
                    sessionUUID);

            if (exchange.getStatusCode() != HttpStatus.OK) {
                log.error("Handshake failed: {}", exchange.getStatusCode());
            }
        }
    }

    //to change the display where the bot takes the screenshots --> change main display
    //monitor
//    private final Rectangle screenRect = new Rectangle(0, 0, 2560, 1440);

    //mac display
    private final Rectangle screenRect = new Rectangle(0, 0, 3456, 2234);

    private BufferedImage singleScreenshot = null;

    private void takeScreenshot(final OutputStream out) {
        try {

            if (this.profile.takeOnlyOneScreenshot) {
                if (this.singleScreenshot == null) {
                    this.singleScreenshot = new Robot().createScreenCapture(this.screenRect);
                }

                ImageIO.write(this.singleScreenshot, "jpg", out);

            } else {

                final BufferedImage capture = new Robot().createScreenCapture(this.screenRect);
                ImageIO.write(capture, "jpg", out);

            }
        } catch (final Exception e) {
            e.printStackTrace();
            IOUtils.closeQuietly(out);
        }
    }

    public static void main(final String[] args) throws Exception {
        new HTTPClientBot(System.getProperties());
    }

    private String getRandomName() {
        return UUID.randomUUID().toString();
    }

    private OAuth2RestTemplate createRestTemplate(final String scopes) {
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        final ClientCredentialsResourceDetails clientCredentialsResourceDetails =
                new ClientCredentialsResourceDetails();
        clientCredentialsResourceDetails
                .setAccessTokenUri(this.profile.webserviceAddress + this.profile.accessTokenEndpoint);
        clientCredentialsResourceDetails.setClientId(this.profile.clientId);
        clientCredentialsResourceDetails.setClientSecret(this.profile.clientSecret);
        if (StringUtils.isBlank(scopes)) {
            clientCredentialsResourceDetails.setScope(Arrays.asList("read", "write"));
        } else {
            clientCredentialsResourceDetails.setScope(
                    Arrays.asList(StringUtils.split(scopes, Constants.LIST_SEPARATOR)));
        }

        final OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(clientCredentialsResourceDetails);
        restTemplate.setErrorHandler(new OAuth2ErrorHandler(clientCredentialsResourceDetails) {

            @Override
            public void handleError(final ClientHttpResponse response) throws IOException {
                System.out.println("********************** handleError: " + response.getStatusCode());
                System.out.println("********************** handleError: " + response.getStatusText());
                super.handleError(response);
            }

        });
        restTemplate
                .getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }

}
