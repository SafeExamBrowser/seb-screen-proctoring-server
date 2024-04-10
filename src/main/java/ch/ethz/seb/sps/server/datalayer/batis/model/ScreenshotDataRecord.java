package ch.ethz.seb.sps.server.datalayer.batis.model;

import javax.annotation.Generated;

public class ScreenshotDataRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-04-09T08:51:01.562+02:00", comments="Source field: screenshot_data.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-04-09T08:51:01.562+02:00", comments="Source field: screenshot_data.session_uuid")
    private String sessionUuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-04-09T08:51:01.562+02:00", comments="Source field: screenshot_data.timestamp")
    private Long timestamp;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-04-09T08:51:01.562+02:00", comments="Source field: screenshot_data.image_format")
    private Integer imageFormat;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-04-09T08:51:01.562+02:00", comments="Source field: screenshot_data.meta_data")
    private String metaData;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-04-09T08:51:01.562+02:00", comments="Source Table: screenshot_data")
    public ScreenshotDataRecord(Long id, String sessionUuid, Long timestamp, Integer imageFormat, String metaData) {
        this.id = id;
        this.sessionUuid = sessionUuid;
        this.timestamp = timestamp;
        this.imageFormat = imageFormat;
        this.metaData = metaData;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-04-09T08:51:01.562+02:00", comments="Source field: screenshot_data.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-04-09T08:51:01.562+02:00", comments="Source field: screenshot_data.session_uuid")
    public String getSessionUuid() {
        return sessionUuid;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-04-09T08:51:01.562+02:00", comments="Source field: screenshot_data.timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-04-09T08:51:01.562+02:00", comments="Source field: screenshot_data.image_format")
    public Integer getImageFormat() {
        return imageFormat;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-04-09T08:51:01.562+02:00", comments="Source field: screenshot_data.meta_data")
    public String getMetaData() {
        return metaData;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table screenshot_data
     *
     * @mbg.generated Tue Apr 09 08:51:01 CEST 2024
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sessionUuid=").append(sessionUuid);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", imageFormat=").append(imageFormat);
        sb.append(", metaData=").append(metaData);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table screenshot_data
     *
     * @mbg.generated Tue Apr 09 08:51:01 CEST 2024
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
            && (this.getSessionUuid() == null ? other.getSessionUuid() == null : this.getSessionUuid().equals(other.getSessionUuid()))
            && (this.getTimestamp() == null ? other.getTimestamp() == null : this.getTimestamp().equals(other.getTimestamp()))
            && (this.getImageFormat() == null ? other.getImageFormat() == null : this.getImageFormat().equals(other.getImageFormat()))
            && (this.getMetaData() == null ? other.getMetaData() == null : this.getMetaData().equals(other.getMetaData()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table screenshot_data
     *
     * @mbg.generated Tue Apr 09 08:51:01 CEST 2024
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSessionUuid() == null) ? 0 : getSessionUuid().hashCode());
        result = prime * result + ((getTimestamp() == null) ? 0 : getTimestamp().hashCode());
        result = prime * result + ((getImageFormat() == null) ? 0 : getImageFormat().hashCode());
        result = prime * result + ((getMetaData() == null) ? 0 : getMetaData().hashCode());
        return result;
    }
}