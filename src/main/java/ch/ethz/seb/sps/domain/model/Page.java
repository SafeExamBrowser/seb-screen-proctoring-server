/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model;

import java.util.Collection;
import java.util.List;

import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.utils.Utils;
import io.swagger.v3.oas.annotations.media.Schema;

/** Data class that defines a Page that corresponds with the SEB Server API page JSON object
 *
 * @param <T> The type of a page entry entity */
public final class Page<T> {

    public static final String ATTR_NAMES_ONLY = "namesOnly";
    public static final String ATTR_NUMBER_OF_PAGES = "numberOfPages";
    public static final String ATTR_PAGE_NUMBER = "pageNumber";
    public static final String ATTR_PAGE_SIZE = "pageSize";
    public static final String ATTR_SORT = "sort";
    public static final String ATTR_CONTENT = "content";

    @Schema(description = "The number of available pages for the specified page size.")
    @JsonProperty(ATTR_NUMBER_OF_PAGES)
    public final int numberOfPages;

    @Schema(description = "The actual page number. Starting with 1.")
    @Size(min = 1)
    @JsonProperty(ATTR_PAGE_NUMBER)
    public final int pageNumber;

    @Schema(description = "The the actual size of a page")
    @Size(min = 1)
    @JsonProperty(ATTR_PAGE_SIZE)
    public final int pageSize;

    @Schema(description = "The page sort column name", nullable = true)
    @JsonProperty(ATTR_SORT)
    public final String sort;

    @Schema(description = "The actual content objects of the page. Might be empty.", nullable = false)
    @JsonProperty(ATTR_CONTENT)
    public final List<T> content;

    @JsonCreator
    public Page(
            @JsonProperty(value = ATTR_NUMBER_OF_PAGES, required = true) final int numberOfPages,
            @JsonProperty(value = ATTR_PAGE_NUMBER, required = true) final int pageNumber,
            @JsonProperty(value = ATTR_PAGE_SIZE, required = true) final int pageSize,
            @JsonProperty(ATTR_SORT) final String sort,
            @JsonProperty(ATTR_CONTENT) final Collection<T> content) {

        this.numberOfPages = numberOfPages;
        this.pageNumber = pageNumber;
        this.content = Utils.immutableListOf(content);
        this.pageSize = pageSize;
        this.sort = sort;
    }

    public int getNumberOfPages() {
        return this.numberOfPages;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public String getSort() {
        return this.sort;
    }

    public Collection<T> getContent() {
        return this.content;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return this.content == null || this.content.isEmpty();
    }

    @Override
    public String toString() {
        return "Page [numberOfPages=" + this.numberOfPages +
                ", pageNumber=" + this.pageNumber +
                ", pageSize=" + this.pageSize +
                ", sort=" + this.sort +
                ", content=" + this.content +
                "]";
    }

}
