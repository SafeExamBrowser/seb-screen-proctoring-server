package ch.ethz.seb.sps.server.datalayer.batis.model;

import javax.annotation.Generated;

public class GroupRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.545+02:00", comments="Source field: seb_group.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.uuid")
    private String uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.name")
    private String name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.description")
    private String description;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.owner")
    private String owner;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.creation_time")
    private Long creationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.last_update_time")
    private Long lastUpdateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.termination_time")
    private Long terminationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.exam_id")
    private Long examId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.545+02:00", comments="Source Table: seb_group")
    public GroupRecord(Long id, String uuid, String name, String description, String owner, Long creationTime, Long lastUpdateTime, Long terminationTime, Long examId) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
        this.examId = examId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.545+02:00", comments="Source field: seb_group.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.uuid")
    public String getUuid() {
        return uuid;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.name")
    public String getName() {
        return name;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.description")
    public String getDescription() {
        return description;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.owner")
    public String getOwner() {
        return owner;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.creation_time")
    public Long getCreationTime() {
        return creationTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.last_update_time")
    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.termination_time")
    public Long getTerminationTime() {
        return terminationTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.546+02:00", comments="Source field: seb_group.exam_id")
    public Long getExamId() {
        return examId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seb_group
     *
     * @mbg.generated Tue May 14 14:28:54 CEST 2024
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", uuid=").append(uuid);
        sb.append(", name=").append(name);
        sb.append(", description=").append(description);
        sb.append(", owner=").append(owner);
        sb.append(", creationTime=").append(creationTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append(", terminationTime=").append(terminationTime);
        sb.append(", examId=").append(examId);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seb_group
     *
     * @mbg.generated Tue May 14 14:28:54 CEST 2024
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
        GroupRecord other = (GroupRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUuid() == null ? other.getUuid() == null : this.getUuid().equals(other.getUuid()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getOwner() == null ? other.getOwner() == null : this.getOwner().equals(other.getOwner()))
            && (this.getCreationTime() == null ? other.getCreationTime() == null : this.getCreationTime().equals(other.getCreationTime()))
            && (this.getLastUpdateTime() == null ? other.getLastUpdateTime() == null : this.getLastUpdateTime().equals(other.getLastUpdateTime()))
            && (this.getTerminationTime() == null ? other.getTerminationTime() == null : this.getTerminationTime().equals(other.getTerminationTime()))
            && (this.getExamId() == null ? other.getExamId() == null : this.getExamId().equals(other.getExamId()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seb_group
     *
     * @mbg.generated Tue May 14 14:28:54 CEST 2024
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUuid() == null) ? 0 : getUuid().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getOwner() == null) ? 0 : getOwner().hashCode());
        result = prime * result + ((getCreationTime() == null) ? 0 : getCreationTime().hashCode());
        result = prime * result + ((getLastUpdateTime() == null) ? 0 : getLastUpdateTime().hashCode());
        result = prime * result + ((getTerminationTime() == null) ? 0 : getTerminationTime().hashCode());
        result = prime * result + ((getExamId() == null) ? 0 : getExamId().hashCode());
        return result;
    }
}