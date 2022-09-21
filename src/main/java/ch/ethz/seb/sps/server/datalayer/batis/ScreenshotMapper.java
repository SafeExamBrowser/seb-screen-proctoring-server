/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.batis;

import java.io.InputStream;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ScreenshotMapper {

    @Select("SELECT ID, CONTENT FROM screenshot WHERE id = #{id}")
    BlobContent selectScreenshotByPK(Long id);

    @Insert("INSERT INTO screenshot (ID, CONTENT) VALUES(#{id}, #{content})")
    void insert(BlobContent blobContent);

    static class BlobContent {
        private Long id;
        private InputStream content;

        public Long getId() {
            return this.id;
        }

        public void setId(final Long id) {
            this.id = id;
        }

        public InputStream getContent() {
            return this.content;
        }

        public void setContent(final InputStream content) {
            this.content = content;
        }
    }

}
