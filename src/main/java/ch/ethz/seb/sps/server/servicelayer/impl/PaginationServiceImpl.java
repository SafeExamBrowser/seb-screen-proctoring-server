/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.Page;
import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.domain.model.service.ScreenshotSearchResult;
import ch.ethz.seb.sps.domain.model.service.SessionSearchResult;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.SessionRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.UserRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.servicelayer.PaginationService;
import ch.ethz.seb.sps.utils.Result;

@Lazy
@Service
public class PaginationServiceImpl implements PaginationService {

    private static final Logger log = LoggerFactory.getLogger(PaginationServiceImpl.class);

    private final int defaultPageSize;
    private final int maxPageSize;

    private final Map<String, Map<String, String>> sortColumnMapping;
    private final Map<String, String> defaultSortColumn;

    public PaginationServiceImpl(
            @Value("${sebserver.webservice.api.pagination.defaultPageSize:10}") final int defaultPageSize,
            @Value("${sebserver.webservice.api.pagination.maxPageSize:500}") final int maxPageSize) {

        this.defaultPageSize = defaultPageSize;
        this.maxPageSize = maxPageSize;
        this.sortColumnMapping = new HashMap<>();
        this.defaultSortColumn = new HashMap<>();
        initSortColumnMapping();
    }

    /** Use this to verify whether native sorting (on SQL level) is supported for a given orderBy column
     * and a given SqlTable or not.
     *
     * @param table SqlTable the SQL table (MyBatis)
     * @param orderBy the orderBy columnName
     * @return true if there is native sorting support for the given attributes */
    @Override
    public boolean isNativeSortingSupported(final SqlTable table, final String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            return false;
        }

        final Map<String, String> tableMap = this.sortColumnMapping.get(table.tableNameAtRuntime());
        if (tableMap == null) {
            return false;
        }

        return tableMap.containsKey(PageSortOrder.decode(orderBy));
    }

    /** Use this to set a page limitation on SQL level. This checks first if there is
     * already a page-limitation set for the local thread and if not, set the default page-limitation */
    @Override
    public void setDefaultLimitIfNotSet() {
        if (PageHelper.getLocalPage() != null) {
            return;
        }
        setPagination(1, this.maxPageSize, null, null);
    }

    @Override
    public void setDefaultLimit() {
        setPagination(1, this.maxPageSize, null, null);
    }

    @Override
    public void setDefaultLimit(final String sort, final String tableName) {
        setPagination(1, this.maxPageSize, sort, tableName);
    }

    @Override
    public int getPageNumber(final Integer pageNumber) {
        return (pageNumber == null)
                ? 1
                : pageNumber;
    }

    /** Get the given pageSize as int type if it is not null and in the range of one to the defined maximum page size.
     * If the given pageSize null or less than one, this returns the defined default page size.
     * If the given pageSize is greater than the defined maximum page size this returns the the defined maximum page
     * size
     *
     * @param pageSize the page size Integer value to convert
     * @return the given pageSize as int type if it is not null and in the range of one to the defined maximum page
     *         size, */
    @Override
    public int getPageSize(final Integer pageSize) {
        return (pageSize == null || pageSize < 1)
                ? this.defaultPageSize
                : (pageSize > this.maxPageSize)
                        ? this.maxPageSize
                        : pageSize;
    }

    @Override
    public <T extends Entity> Result<Page<T>> getPage(
            final Integer pageNumber,
            final Integer pageSize,
            final String sort,
            final String tableName,
            final Supplier<Result<Collection<T>>> delegate) {

        return Result.tryCatch(() -> {
            //final SqlTable table = SqlTable.of(tableName);
            final com.github.pagehelper.Page<Object> page =
                    setPagination(pageNumber, this.getPageSize(pageSize), sort, tableName);

            final Collection<T> list = delegate.get().getOrThrow();

            return new Page<>(
                    page.getPages(),
                    page.getPageNum(),
                    this.getPageSize(pageSize),
                    sort,
                    list);
        });
    }

    @Override
    public <T extends Entity> Result<Page<T>> getPage(
            final Integer pageNumber,
            final Integer pageSize,
            final String sort,
            final String tableName,
            final Runnable preProcessor,
            final Supplier<Result<Collection<T>>> delegate) {

        if (preProcessor != null) {
            try {
                preProcessor.run();
            } catch (final Exception e) {
                log.error("Failed to apply pagination pre processing: ", e);
            }
        }
        return getPage(pageNumber, pageSize, sort, tableName, delegate);
    }

    @Override
    public <T> Result<Page<T>> getPageOf(
            final Integer pageNumber,
            final Integer pageSize,
            final String sort,
            final String tableName,
            final Supplier<Result<Collection<T>>> delegate) {

        return Result.tryCatch(() -> {
            //final SqlTable table = SqlTable.of(tableName);
            final com.github.pagehelper.Page<Object> page =
                    setPagination(
                            pageNumber,
                            this.getPageSize(pageSize),
                            sort,
                            tableName);

            final Collection<T> list = delegate.get().getOrThrow();

            return new Page<>(
                    page.getPages(),
                    page.getPageNum(),
                    this.getPageSize(pageSize),
                    sort,
                    list);
        });
    }

    @Override
    public <T> Result<Page<T>> getPageOf(
            final Integer pageNumber,
            final Integer pageSize,
            final String sort,
            final String tableName,
            final Runnable preProcessor,
            final Supplier<Result<Collection<T>>> delegate) {

        if (preProcessor != null) {
            try {
                preProcessor.run();
            } catch (final Exception e) {
                log.error("Failed to apply pagination pre processing: ", e);
            }
        }
        return getPageOf(pageNumber, pageSize, sort, tableName, delegate);
    }

    private String verifySortColumnName(final String sort, final String columnName) {

        if (StringUtils.isBlank(sort)) {
            return this.defaultSortColumn.get(columnName);
        }

        final Map<String, String> mapping = this.sortColumnMapping.get(columnName);
        if (mapping != null) {
            final String sortColumn = PageSortOrder.decode(sort);
            if (StringUtils.isBlank(sortColumn)) {
                return this.defaultSortColumn.get(columnName);
            }
            return mapping.get(sortColumn);
        }

        return this.defaultSortColumn.get(columnName);
    }

    @Override
    public void setUnlimitedPagination(final String sort, final String tableName) {

        PageHelper.startPage(1, 5000, true, true, false);

        if (StringUtils.isNotBlank(tableName) && StringUtils.isNotBlank(sort)) {
            final PageSortOrder sortOrder = PageSortOrder.getSortOrder(sort);
            final String sortColumnName = verifySortColumnName(sort, tableName);
            if (StringUtils.isNotBlank(sortColumnName)) {
                switch (sortOrder) {
                    case DESCENDING: {
                        PageHelper.orderBy(sortColumnName + " DESC, id DESC");
                        break;
                    }
                    default: {
                        PageHelper.orderBy(sortColumnName + ", id");
                        break;
                    }
                }
            }
        }
    }

    private com.github.pagehelper.Page<Object> setPagination(
            final Integer pageNumber,
            final Integer pageSize,
            final String sort,
            final String sortMappingName) {

        final com.github.pagehelper.Page<Object> startPage =
                PageHelper.startPage(getPageNumber(pageNumber), getPageSize(pageSize), true, true, false);

        if (StringUtils.isNotBlank(sortMappingName) && StringUtils.isNotBlank(sort)) {
            final PageSortOrder sortOrder = PageSortOrder.getSortOrder(sort);
            final String sortColumnName = verifySortColumnName(sort, sortMappingName);
            if (StringUtils.isNotBlank(sortColumnName)) {
                switch (sortOrder) {
                    case DESCENDING: {
                        PageHelper.orderBy(sortColumnName + " DESC, id DESC");
                        break;
                    }
                    default: {
                        PageHelper.orderBy(sortColumnName + ", id");
                        break;
                    }
                }
            }
        }

        return startPage;
    }

    private void initSortColumnMapping() {

        // define and initialize sort column mapping for...

        // User Table
        final Map<String, String> userTableMap = new HashMap<>();
        userTableMap.put(Domain.USER.ATTR_NAME, UserRecordDynamicSqlSupport.name.name());
        userTableMap.put(Domain.USER.ATTR_SURNAME, UserRecordDynamicSqlSupport.surname.name());
        userTableMap.put(Domain.USER.ATTR_USERNAME, UserRecordDynamicSqlSupport.username.name());
        userTableMap.put(Domain.USER.ATTR_EMAIL, UserRecordDynamicSqlSupport.email.name());
        userTableMap.put(Domain.USER.ATTR_LANGUAGE, UserRecordDynamicSqlSupport.language.name());
        userTableMap.put(Domain.USER.ATTR_CREATION_TIME, UserRecordDynamicSqlSupport.creationTime.name());
        this.sortColumnMapping.put(UserRecordDynamicSqlSupport.userRecord.tableNameAtRuntime(), userTableMap);
        this.defaultSortColumn.put(UserRecordDynamicSqlSupport.userRecord.tableNameAtRuntime(), Domain.USER.ATTR_ID);

        // Group Table
        final Map<String, String> groupTableMap = new HashMap<>();
        groupTableMap.put(
                Domain.SEB_GROUP.ATTR_NAME,
                GroupRecordDynamicSqlSupport.name.name());
        groupTableMap.put(
                Domain.SEB_GROUP.ATTR_CREATION_TIME,
                GroupRecordDynamicSqlSupport.creationTime.name());
        groupTableMap.put(
                Domain.SEB_GROUP.ATTR_TERMINATION_TIME,
                GroupRecordDynamicSqlSupport.terminationTime.name());
        this.sortColumnMapping.put(GroupRecordDynamicSqlSupport.groupRecord.tableNameAtRuntime(), groupTableMap);
        this.defaultSortColumn.put(GroupRecordDynamicSqlSupport.groupRecord.tableNameAtRuntime(),
                Domain.SEB_GROUP.ATTR_NAME);

        // Session Table
        final Map<String, String> sessionTableMap = new HashMap<>();
        sessionTableMap.put(
                Domain.SESSION.ATTR_CREATION_TIME,
                SessionRecordDynamicSqlSupport.creationTime.name());
        sessionTableMap.put(
                Domain.SESSION.ATTR_IMAGE_FORMAT,
                SessionRecordDynamicSqlSupport.imageFormat.name());
        sessionTableMap.put(
                SessionSearchResult.ATTR_START_TIME,
                SessionRecordDynamicSqlSupport.creationTime.name());
        sessionTableMap.put(
                Domain.SESSION.ATTR_CLIENT_NAME,
                SessionRecordDynamicSqlSupport.clientName.name());
        sessionTableMap.put(
                Domain.SESSION.ATTR_CLIENT_IP,
                SessionRecordDynamicSqlSupport.clientIp.name());
        sessionTableMap.put(
                Domain.SESSION.ATTR_CLIENT_OS_NAME,
                SessionRecordDynamicSqlSupport.clientOsName.name());
        sessionTableMap.put(
                Domain.SESSION.ATTR_CLIENT_VERSION,
                SessionRecordDynamicSqlSupport.clientVersion.name());
        sessionTableMap.put(
                Domain.SESSION.ATTR_CLIENT_MACHINE_NAME,
                SessionRecordDynamicSqlSupport.clientMachineName.name());
        this.sortColumnMapping.put(SessionRecordDynamicSqlSupport.sessionRecord.tableNameAtRuntime(), sessionTableMap);
        this.defaultSortColumn.put(SessionRecordDynamicSqlSupport.sessionRecord.tableNameAtRuntime(),
                Domain.SESSION.ATTR_CLIENT_NAME);

        // Screenshot Table
        final Map<String, String> screenshotDataTableMap = new HashMap<>();
        screenshotDataTableMap.put(
                Domain.SCREENSHOT_DATA.ATTR_TIMESTAMP,
                ScreenshotDataRecordDynamicSqlSupport.timestamp.name());
        screenshotDataTableMap.put(
                ScreenshotSearchResult.ATTR_TIMESTAMP,
                ScreenshotDataRecordDynamicSqlSupport.timestamp.name());
        screenshotDataTableMap.put(
                ScreenshotSearchResult.ATTR_SESSION_START_TIME,
                SessionRecordDynamicSqlSupport.creationTime.name());
        screenshotDataTableMap.put(
                ScreenshotSearchResult.ATTR_SESSION_END_TIME,
                SessionRecordDynamicSqlSupport.terminationTime.name());
        screenshotDataTableMap.put(
                ScreenshotSearchResult.ATTR_SESSION_CLIENT_NAME,
                SessionRecordDynamicSqlSupport.clientName.name());
        screenshotDataTableMap.put(
                ScreenshotSearchResult.ATTR_SESSION_CLIENT_IP,
                SessionRecordDynamicSqlSupport.clientIp.name());
        screenshotDataTableMap.put(
                ScreenshotSearchResult.ATTR_SESSION_CLIENT_MACHINE_NAME,
                SessionRecordDynamicSqlSupport.clientMachineName.name());
        screenshotDataTableMap.put(
                ScreenshotSearchResult.ATTR_SESSION_CLIENT_OS_NAME,
                SessionRecordDynamicSqlSupport.clientOsName.name());
        screenshotDataTableMap.put(
                ScreenshotSearchResult.ATTR_SESSION_CLIENT_VERSION,
                SessionRecordDynamicSqlSupport.clientVersion.name());
        this.sortColumnMapping.put(ScreenshotDataRecordDynamicSqlSupport.screenshotDataRecord.tableNameAtRuntime(),
                screenshotDataTableMap);
        this.defaultSortColumn.put(
                ScreenshotDataRecordDynamicSqlSupport.screenshotDataRecord.tableNameAtRuntime(),
                Domain.SCREENSHOT_DATA.ATTR_TIMESTAMP);

        // TODO define sort mapping for other tables

    }

}
