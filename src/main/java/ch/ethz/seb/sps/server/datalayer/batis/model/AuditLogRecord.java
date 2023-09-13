package ch.ethz.seb.sps.server.datalayer.batis.model;

import javax.annotation.Generated;

public class AuditLogRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: audit_log.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: audit_log.user_uuid")
    private String userUuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: audit_log.timestamp")
    private Long timestamp;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: audit_log.activity_type")
    private String activityType;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: audit_log.entity_type")
    private String entityType;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: audit_log.entity_id")
    private Long entityId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: audit_log.message")
    private String message;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source Table: audit_log")
    public AuditLogRecord(Long id, String userUuid, Long timestamp, String activityType, String entityType, Long entityId, String message) {
        this.id = id;
        this.userUuid = userUuid;
        this.timestamp = timestamp;
        this.activityType = activityType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.message = message;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: audit_log.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: audit_log.user_uuid")
    public String getUserUuid() {
        return userUuid;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: audit_log.timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: audit_log.activity_type")
    public String getActivityType() {
        return activityType;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: audit_log.entity_type")
    public String getEntityType() {
        return entityType;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: audit_log.entity_id")
    public Long getEntityId() {
        return entityId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: audit_log.message")
    public String getMessage() {
        return message;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table audit_log
     *
     * @mbg.generated Thu Sep 07 09:53:44 CEST 2023
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userUuid=").append(userUuid);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", activityType=").append(activityType);
        sb.append(", entityType=").append(entityType);
        sb.append(", entityId=").append(entityId);
        sb.append(", message=").append(message);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table audit_log
     *
     * @mbg.generated Thu Sep 07 09:53:44 CEST 2023
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
        AuditLogRecord other = (AuditLogRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserUuid() == null ? other.getUserUuid() == null : this.getUserUuid().equals(other.getUserUuid()))
            && (this.getTimestamp() == null ? other.getTimestamp() == null : this.getTimestamp().equals(other.getTimestamp()))
            && (this.getActivityType() == null ? other.getActivityType() == null : this.getActivityType().equals(other.getActivityType()))
            && (this.getEntityType() == null ? other.getEntityType() == null : this.getEntityType().equals(other.getEntityType()))
            && (this.getEntityId() == null ? other.getEntityId() == null : this.getEntityId().equals(other.getEntityId()))
            && (this.getMessage() == null ? other.getMessage() == null : this.getMessage().equals(other.getMessage()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table audit_log
     *
     * @mbg.generated Thu Sep 07 09:53:44 CEST 2023
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserUuid() == null) ? 0 : getUserUuid().hashCode());
        result = prime * result + ((getTimestamp() == null) ? 0 : getTimestamp().hashCode());
        result = prime * result + ((getActivityType() == null) ? 0 : getActivityType().hashCode());
        result = prime * result + ((getEntityType() == null) ? 0 : getEntityType().hashCode());
        result = prime * result + ((getEntityId() == null) ? 0 : getEntityId().hashCode());
        result = prime * result + ((getMessage() == null) ? 0 : getMessage().hashCode());
        return result;
    }
}