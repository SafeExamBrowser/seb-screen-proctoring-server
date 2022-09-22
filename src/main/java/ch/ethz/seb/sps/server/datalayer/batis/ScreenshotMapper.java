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

    @Select("SELECT id, image FROM screenshot WHERE id = #{id}")
    BlobContent selectScreenshotByPK(Long id);

    @Insert("INSERT INTO screenshot (id, image) VALUES(#{id}, #{image})")
    void insert(BlobContent blobContent);

    public static class BlobContent {

        private Long id;
        private InputStream image;

        public BlobContent(final Long id, final InputStream image) {
            this.id = id;
            this.image = image;
        }

        public Long getId() {
            return this.id;
        }

        public void setId(final Long id) {
            this.id = id;
        }

        public InputStream getImage() {
            return this.image;
        }

        public void setImage(final InputStream image) {
            this.image = image;
        }

    }

}
