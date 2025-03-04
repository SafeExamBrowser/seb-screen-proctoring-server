package ch.ethz.seb.sps.server.datalayer.batis.model;

import jakarta.annotation.Generated;

public class ExamRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.uuid")
    private String uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.name")
    private String name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.description")
    private String description;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.url")
    private String url;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.type")
    private String type;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.owner")
    private String owner;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.supporter")
    private String supporter;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.creation_time")
    private Long creationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.last_update_time")
    private Long lastUpdateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.termination_time")
    private Long terminationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.start_time")
    private Long startTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.end_time")
    private Long endTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.deletion_time")
    private Long deletionTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source Table: exam")
    public ExamRecord(Long id, String uuid, String name, String description, String url, String type, String owner, String supporter, Long creationTime, Long lastUpdateTime, Long terminationTime, Long startTime, Long endTime, Long deletionTime) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.url = url;
        this.type = type;
        this.owner = owner;
        this.supporter = supporter;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deletionTime = deletionTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.uuid")
    public String getUuid() {
        return uuid;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.name")
    public String getName() {
        return name;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.description")
    public String getDescription() {
        return description;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.url")
    public String getUrl() {
        return url;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.type")
    public String getType() {
        return type;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.owner")
    public String getOwner() {
        return owner;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.supporter")
    public String getSupporter() {
        return supporter;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.creation_time")
    public Long getCreationTime() {
        return creationTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.last_update_time")
    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.termination_time")
    public Long getTerminationTime() {
        return terminationTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.start_time")
    public Long getStartTime() {
        return startTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.end_time")
    public Long getEndTime() {
        return endTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.597+01:00", comments="Source field: exam.deletion_time")
    public Long getDeletionTime() {
        return deletionTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table exam
     *
     * @mbg.generated Mon Nov 18 10:46:20 CET 2024
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
        sb.append(", url=").append(url);
        sb.append(", type=").append(type);
        sb.append(", owner=").append(owner);
        sb.append(", supporter=").append(supporter);
        sb.append(", creationTime=").append(creationTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append(", terminationTime=").append(terminationTime);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", deletionTime=").append(deletionTime);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table exam
     *
     * @mbg.generated Mon Nov 18 10:46:20 CET 2024
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
        ExamRecord other = (ExamRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUuid() == null ? other.getUuid() == null : this.getUuid().equals(other.getUuid()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getUrl() == null ? other.getUrl() == null : this.getUrl().equals(other.getUrl()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getOwner() == null ? other.getOwner() == null : this.getOwner().equals(other.getOwner()))
            && (this.getSupporter() == null ? other.getSupporter() == null : this.getSupporter().equals(other.getSupporter()))
            && (this.getCreationTime() == null ? other.getCreationTime() == null : this.getCreationTime().equals(other.getCreationTime()))
            && (this.getLastUpdateTime() == null ? other.getLastUpdateTime() == null : this.getLastUpdateTime().equals(other.getLastUpdateTime()))
            && (this.getTerminationTime() == null ? other.getTerminationTime() == null : this.getTerminationTime().equals(other.getTerminationTime()))
            && (this.getStartTime() == null ? other.getStartTime() == null : this.getStartTime().equals(other.getStartTime()))
            && (this.getEndTime() == null ? other.getEndTime() == null : this.getEndTime().equals(other.getEndTime()))
            && (this.getDeletionTime() == null ? other.getDeletionTime() == null : this.getDeletionTime().equals(other.getDeletionTime()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table exam
     *
     * @mbg.generated Mon Nov 18 10:46:20 CET 2024
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUuid() == null) ? 0 : getUuid().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getOwner() == null) ? 0 : getOwner().hashCode());
        result = prime * result + ((getSupporter() == null) ? 0 : getSupporter().hashCode());
        result = prime * result + ((getCreationTime() == null) ? 0 : getCreationTime().hashCode());
        result = prime * result + ((getLastUpdateTime() == null) ? 0 : getLastUpdateTime().hashCode());
        result = prime * result + ((getTerminationTime() == null) ? 0 : getTerminationTime().hashCode());
        result = prime * result + ((getStartTime() == null) ? 0 : getStartTime().hashCode());
        result = prime * result + ((getEndTime() == null) ? 0 : getEndTime().hashCode());
        result = prime * result + ((getDeletionTime() == null) ? 0 : getDeletionTime().hashCode());
        return result;
    }
}