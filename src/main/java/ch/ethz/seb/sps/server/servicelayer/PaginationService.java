/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.mybatis.dynamic.sql.SqlTable;

import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.Page;
import ch.ethz.seb.sps.utils.Result;

/** A service to apply pagination functionality within collection results from data access layer.
 * The default implementation uses Mybatis-PageHelper to apply the pagination on SQL level where possible:
 * https://github.com/pagehelper/Mybatis-PageHelper */

public interface PaginationService {

    /** Use this to verify whether native sorting (on SQL level) is supported for a given orderBy column
     * and a given SqlTable or not.
     *
     * @param table SqlTable the SQL table (MyBatis)
     * @param orderBy the orderBy columnName
     * @return true if there is native sorting support for the given attributes */
    boolean isNativeSortingSupported(SqlTable table, String orderBy);

    /** Use this to set a page limitation on SQL level. This checks first if there is
     * already a page-limitation set for the local thread and if not, set the default page-limitation */
    void setDefaultLimitIfNotSet();

    void setDefaultLimit();

    void setDefaultLimit(String sort, String tableName);

    int getPageNumber(Integer pageNumber);

    /** Get the given pageSize as int type if it is not null and in the range of one to the defined maximum page size.
     * If the given pageSize null or less then one, this returns the defined default page size.
     * If the given pageSize is greater then the defined maximum page size this returns the the defined maximum page
     * size
     *
     * @param pageSize the page size Integer value to convert
     * @return the given pageSize as int type if it is not null and in the range of one to the defined maximum page
     *         size, */
    int getPageSize(final Integer pageSize);

    /** Get a Page of specified domain models from given pagination attributes within collection supplier delegate.
     * <p>
     * NOTE: Paging always depends on SQL level. It depends on the collection given by the SQL select statement
     * that is executed within MyBatis by using the MyBatis page service.
     * Be aware that if the delegate that is given here applies an additional filter to the filtering done
     * on SQL level, this will lead to paging with not fully filled pages or even to empty pages if the filter
     * filters a lot of the entries given by the SQL statement away.
     * So we recommend to apply as much of the filtering as possible on the SQL level and only if necessary and
     * not avoidable, apply a additional filter on software-level that eventually filter one or two entities
     * for a page.
     *
     * @param pageNumber the current page number
     * @param pageSize the (full) size of the page
     * @param sort the name of the sort column with a leading '-' for descending sort order
     * @param tableName the name of the SQL table on which the pagination is applying to
     * @param delegate a collection supplier the does the underling SQL query with specified pagination attributes
     * @return Result refers to a Page of specified type of model models or to an exception on error case */
    <T extends Entity> Result<Page<T>> getPage(
            Integer pageNumber,
            Integer pageSize,
            String sort,
            String tableName,
            Supplier<Result<Collection<T>>> delegate);

    /** Get a Page of specified domain models from given pagination attributes within collection supplier delegate.
     * <p>
     * NOTE: Paging always depends on SQL level. It depends on the collection given by the SQL select statement
     * that is executed within MyBatis by using the MyBatis page service.
     * Be aware that if the delegate that is given here applies an additional filter to the filtering done
     * on SQL level, this will lead to paging with not fully filled pages or even to empty pages if the filter
     * filters a lot of the entries given by the SQL statement away.
     * So we recommend to apply as much of the filtering as possible on the SQL level and only if necessary and
     * not avoidable, apply a additional filter on software-level that eventually filter one or two entities
     * for a page.
     *
     * @param pageNumber the current page number
     * @param pageSize the (full) size of the page
     * @param sort the name of the sort column with a leading '-' for descending sort order
     * @param tableName the name of the SQL table on which the pagination is applying to
     * @param preProcessor A pre-processor block that gets executed before the pagination attributes are set to thread
     *            local to be applied to next (following) SQL query. This is especially useful if there must be done
     *            some SQL queries before the the paged query is applied.
     * @param delegate a collection supplier the does the underling SQL query with specified pagination attributes
     * @return Result refers to a Page of specified type of model models or to an exception on error case */
    <T extends Entity> Result<Page<T>> getPage(
            Integer pageNumber,
            Integer pageSize,
            String sort,
            String tableName,
            Runnable preProcessor,
            Supplier<Result<Collection<T>>> delegate);

    /** Fetches a paged batch of objects
     * <p>
     * NOTE: Paging always depends on SQL level. It depends on the collection given by the SQL select statement
     * that is executed within MyBatis by using the MyBatis page service.
     * Be aware that if the delegate that is given here applies an additional filter to the filtering done
     * on SQL level, this will lead to paging with not fully filled pages or even to empty pages if the filter
     * filters a lot of the entries given by the SQL statement away.
     * So we recommend to apply as much of the filtering as possible on the SQL level and only if necessary and
     * not avoidable, apply a additional filter on software-level that eventually filter one or two entities
     * for a page.
     *
     * @param pageNumber the current page number
     * @param pageSize the (full) size of the page
     * @param sort the name of the sort column with a leading '-' for descending sort order
     * @param tableName the name of the SQL table on which the pagination is applying to
     * @param delegate a collection supplier the does the underling SQL query with specified pagination attributes
     * @return Result refers to a Collection of specified type of objects or to an exception on error case */
    <T> Result<Page<T>> getPageOf(
            Integer pageNumber,
            Integer pageSize,
            String sort,
            String tableName,
            Supplier<Result<Collection<T>>> delegate);

    /** Fetches a paged batch of objects
     * <p>
     * NOTE: Paging always depends on SQL level. It depends on the collection given by the SQL select statement
     * that is executed within MyBatis by using the MyBatis page service.
     * Be aware that if the delegate that is given here applies an additional filter to the filtering done
     * on SQL level, this will lead to paging with not fully filled pages or even to empty pages if the filter
     * filters a lot of the entries given by the SQL statement away.
     * So we recommend to apply as much of the filtering as possible on the SQL level and only if necessary and
     * not avoidable, apply a additional filter on software-level that eventually filter one or two entities
     * for a page.
     *
     * @param pageNumber the current page number
     * @param pageSize the (full) size of the page
     * @param sort the name of the sort column with a leading '-' for descending sort order
     * @param tableName the name of the SQL table on which the pagination is applying to
     * @param preProcessor A pre-processor block that gets executed before the pagination attributes are set to thread
     *            local to be applied to next (following) SQL query. This is especially useful if there must be done
     *            some SQL queries before the the paged query is applied.
     * @param delegate a collection supplier the does the underling SQL query with specified pagination attributes
     * @return Result refers to a Collection of specified type of objects or to an exception on error case */
    <T> Result<Page<T>> getPageOf(
            Integer pageNumber,
            Integer pageSize,
            String sort,
            String tableName,
            Runnable preProcessor,
            Supplier<Result<Collection<T>>> delegate);

    /** This sets a unlimited page fetch with filtering and sort on DB level for the next applying
     * SQL call. This can be used in cases where pagination shall be done on back-end level but
     * filter and sort still apply to database level. One can fetch a fully filtered and sorted page
     * here with all elements found by the query and apply effective page afterwards.
     * There is of course still an upper limit of 5000 entries that can be fetched with this
     *
     * @param sort sort the name of the sort column with a leading '-' for descending sort order
     * @param tableName the name of the SQL table on which the pagination is applying to */
    void setUnlimitedPagination(
            String sort,
            String tableName);

    /** Use this to build a current Page from a given list of objects.
     *
     * @param <T> the Type if list entities
     * @param pageNumber the number of the current page
     * @param pageSize the size of a page
     * @param sort the page sort flag
     * @param all list of all entities, unsorted
     * @param pageFunction a function that filter and sorts the list for specific type of entries
     * @return current page of objects from the sorted list of entities */
    default <T> Page<T> buildPageFromList(
            final Integer pageNumber,
            final Integer pageSize,
            final String sort,
            final Collection<T> all,
            final Function<Collection<T>, List<T>> pageFunction) {

        final List<T> sorted = pageFunction.apply(all);

        int _pageNumber = getPageNumber(pageNumber);
        final int _pageSize = getPageSize(pageSize);

        int start = (_pageNumber - 1) * _pageSize;
        if (start >= sorted.size()) {
            start = 0;
            _pageNumber = 1;
        }
        int end = start + _pageSize;
        if (sorted.size() < end) {
            end = sorted.size();
        }
        int numberOfPages = sorted.size() / _pageSize;
        if (sorted.size() % _pageSize > 0) {
            numberOfPages++;
        }

        return new Page<>(
                (numberOfPages > 0) ? numberOfPages : 1,
                _pageNumber,
                _pageSize,
                sort,
                sorted.subList(start, end));
    }

}
