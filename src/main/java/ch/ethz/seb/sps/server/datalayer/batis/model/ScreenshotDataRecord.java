package ch.ethz.seb.sps.server.datalayer.batis.model;

import javax.annotation.Generated;

public class ScreenshotDataRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.757+02:00", comments="Source field: SCREENSHOT_DATA.ID")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.758+02:00", comments="Source field: SCREENSHOT_DATA.SCREENSHOT_ID")
    private Long screenshotId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.758+02:00", comments="Source field: SCREENSHOT_DATA.SESSION_ID")
    private String sessionId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.758+02:00", comments="Source field: SCREENSHOT_DATA.GROUPID")
    private Long groupid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.758+02:00", comments="Source field: SCREENSHOT_DATA.TIMESTAMP")
    private Long timestamp;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.758+02:00", comments="Source field: SCREENSHOT_DATA.IMAGE_URL")
    private String imageUrl;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.758+02:00", comments="Source field: SCREENSHOT_DATA.IMAGE_FORMAT")
    private String imageFormat;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.759+02:00", comments="Source field: SCREENSHOT_DATA.META_DATA")
    private String metaData;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.757+02:00", comments="Source Table: SCREENSHOT_DATA")
    public ScreenshotDataRecord(Long id, Long screenshotId, String sessionId, Long groupid, Long timestamp, String imageUrl, String imageFormat, String metaData) {
        this.id = id;
        this.screenshotId = screenshotId;
        this.sessionId = sessionId;
        this.groupid = groupid;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
        this.imageFormat = imageFormat;
        this.metaData = metaData;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.757+02:00", comments="Source field: SCREENSHOT_DATA.ID")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.758+02:00", comments="Source field: SCREENSHOT_DATA.SCREENSHOT_ID")
    public Long getScreenshotId() {
        return screenshotId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.758+02:00", comments="Source field: SCREENSHOT_DATA.SESSION_ID")
    public String getSessionId() {
        return sessionId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.758+02:00", comments="Source field: SCREENSHOT_DATA.GROUPID")
    public Long getGroupid() {
        return groupid;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.758+02:00", comments="Source field: SCREENSHOT_DATA.TIMESTAMP")
    public Long getTimestamp() {
        return timestamp;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.758+02:00", comments="Source field: SCREENSHOT_DATA.IMAGE_URL")
    public String getImageUrl() {
        return imageUrl;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.758+02:00", comments="Source field: SCREENSHOT_DATA.IMAGE_FORMAT")
    public String getImageFormat() {
        return imageFormat;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.759+02:00", comments="Source field: SCREENSHOT_DATA.META_DATA")
    public String getMetaData() {
        return metaData;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SCREENSHOT_DATA
     *
     * @mbg.generated Wed Sep 21 14:52:03 CEST 2022
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", screenshotId=").append(screenshotId);
        sb.append(", sessionId=").append(sessionId);
        sb.append(", groupid=").append(groupid);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", imageUrl=").append(imageUrl);
        sb.append(", imageFormat=").append(imageFormat);
        sb.append(", metaData=").append(metaData);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SCREENSHOT_DATA
     *
     * @mbg.generated Wed Sep 21 14:52:03 CEST 2022
     */
    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ScreenshotDataRecord other = (ScreenshotDataRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getScreenshotId() == null ? other.getScreenshotId() == null : this.getScreenshotId().equals(other.getScreenshotId()))
            && (this.getSessionId() == null ? other.getSessionId() == null : this.getSessionId().equals(other.getSessionId()))
            && (this.getGroupid() == null ? other.getGroupid() == null : this.getGroupid().equals(other.getGroupid()))
            && (this.getTimestamp() == null ? other.getTimestamp() == null : this.getTimestamp().equals(other.getTimestamp()))
            && (this.getImageUrl() == null ? other.getImageUrl() == null : this.getImageUrl().equals(other.getImageUrl()))
            && (this.getImageFormat() == null ? other.getImageFormat() == null : this.getImageFormat().equals(other.getImageFormat()))
            && (this.getMetaData() == null ? other.getMetaData() == null : this.getMetaData().equals(other.getMetaData()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SCREENSHOT_DATA
     *
     * @mbg.generated Wed Sep 21 14:52:03 CEST 2022
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getScreenshotId() == null) ? 0 : getScreenshotId().hashCode());
        result = prime * result + ((getSessionId() == null) ? 0 : getSessionId().hashCode());
        result = prime * result + ((getGroupid() == null) ? 0 : getGroupid().hashCode());
        result = prime * result + ((getTimestamp() == null) ? 0 : getTimestamp().hashCode());
        result = prime * result + ((getImageUrl() == null) ? 0 : getImageUrl().hashCode());
        result = prime * result + ((getImageFormat() == null) ? 0 : getImageFormat().hashCode());
        result = prime * result + ((getMetaData() == null) ? 0 : getMetaData().hashCode());
        return result;
    }
}