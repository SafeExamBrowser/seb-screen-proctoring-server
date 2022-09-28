package ch.ethz.seb.sps.server.datalayer.batis.model;

import javax.annotation.Generated;

public class SessionRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.514+02:00", comments="Source field: session.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.514+02:00", comments="Source field: session.group_id")
    private Long groupId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.514+02:00", comments="Source field: session.uuid")
    private String uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.514+02:00", comments="Source field: session.name")
    private String name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.515+02:00", comments="Source field: session.creation_time")
    private Long creationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.515+02:00", comments="Source field: session.termination_time")
    private Long terminationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.513+02:00", comments="Source Table: session")
    public SessionRecord(Long id, Long groupId, String uuid, String name, Long creationTime, Long terminationTime) {
        this.id = id;
        this.groupId = groupId;
        this.uuid = uuid;
        this.name = name;
        this.creationTime = creationTime;
        this.terminationTime = terminationTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.514+02:00", comments="Source field: session.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.514+02:00", comments="Source field: session.group_id")
    public Long getGroupId() {
        return groupId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.514+02:00", comments="Source field: session.uuid")
    public String getUuid() {
        return uuid;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.514+02:00", comments="Source field: session.name")
    public String getName() {
        return name;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.515+02:00", comments="Source field: session.creation_time")
    public Long getCreationTime() {
        return creationTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.515+02:00", comments="Source field: session.termination_time")
    public Long getTerminationTime() {
        return terminationTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table session
     *
     * @mbg.generated Tue Sep 27 17:51:00 CEST 2022
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", groupId=").append(groupId);
        sb.append(", uuid=").append(uuid);
        sb.append(", name=").append(name);
        sb.append(", creationTime=").append(creationTime);
        sb.append(", terminationTime=").append(terminationTime);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table session
     *
     * @mbg.generated Tue Sep 27 17:51:00 CEST 2022
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
        SessionRecord other = (SessionRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGroupId() == null ? other.getGroupId() == null : this.getGroupId().equals(other.getGroupId()))
            && (this.getUuid() == null ? other.getUuid() == null : this.getUuid().equals(other.getUuid()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getCreationTime() == null ? other.getCreationTime() == null : this.getCreationTime().equals(other.getCreationTime()))
            && (this.getTerminationTime() == null ? other.getTerminationTime() == null : this.getTerminationTime().equals(other.getTerminationTime()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table session
     *
     * @mbg.generated Tue Sep 27 17:51:00 CEST 2022
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGroupId() == null) ? 0 : getGroupId().hashCode());
        result = prime * result + ((getUuid() == null) ? 0 : getUuid().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getCreationTime() == null) ? 0 : getCreationTime().hashCode());
        result = prime * result + ((getTerminationTime() == null) ? 0 : getTerminationTime().hashCode());
        return result;
    }
}