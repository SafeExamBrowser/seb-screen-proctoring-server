package ch.ethz.seb.sps.server.datalayer.batis.model;

import javax.annotation.Generated;

public class EntityPrivilegeRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.880+02:00", comments="Source field: entity_privilege.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.880+02:00", comments="Source field: entity_privilege.entity_type")
    private String entityType;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.880+02:00", comments="Source field: entity_privilege.entity_id")
    private Long entityId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.880+02:00", comments="Source field: entity_privilege.user_uuid")
    private String userUuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.880+02:00", comments="Source field: entity_privilege.privileges")
    private String privileges;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.880+02:00", comments="Source Table: entity_privilege")
    public EntityPrivilegeRecord(Long id, String entityType, Long entityId, String userUuid, String privileges) {
        this.id = id;
        this.entityType = entityType;
        this.entityId = entityId;
        this.userUuid = userUuid;
        this.privileges = privileges;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.880+02:00", comments="Source field: entity_privilege.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.880+02:00", comments="Source field: entity_privilege.entity_type")
    public String getEntityType() {
        return entityType;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.880+02:00", comments="Source field: entity_privilege.entity_id")
    public Long getEntityId() {
        return entityId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.880+02:00", comments="Source field: entity_privilege.user_uuid")
    public String getUserUuid() {
        return userUuid;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.880+02:00", comments="Source field: entity_privilege.privileges")
    public String getPrivileges() {
        return privileges;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table entity_privilege
     *
     * @mbg.generated Tue Aug 15 14:43:27 CEST 2023
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", entityType=").append(entityType);
        sb.append(", entityId=").append(entityId);
        sb.append(", userUuid=").append(userUuid);
        sb.append(", privileges=").append(privileges);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table entity_privilege
     *
     * @mbg.generated Tue Aug 15 14:43:27 CEST 2023
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
        EntityPrivilegeRecord other = (EntityPrivilegeRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getEntityType() == null ? other.getEntityType() == null : this.getEntityType().equals(other.getEntityType()))
            && (this.getEntityId() == null ? other.getEntityId() == null : this.getEntityId().equals(other.getEntityId()))
            && (this.getUserUuid() == null ? other.getUserUuid() == null : this.getUserUuid().equals(other.getUserUuid()))
            && (this.getPrivileges() == null ? other.getPrivileges() == null : this.getPrivileges().equals(other.getPrivileges()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table entity_privilege
     *
     * @mbg.generated Tue Aug 15 14:43:27 CEST 2023
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getEntityType() == null) ? 0 : getEntityType().hashCode());
        result = prime * result + ((getEntityId() == null) ? 0 : getEntityId().hashCode());
        result = prime * result + ((getUserUuid() == null) ? 0 : getUserUuid().hashCode());
        result = prime * result + ((getPrivileges() == null) ? 0 : getPrivileges().hashCode());
        return result;
    }
}