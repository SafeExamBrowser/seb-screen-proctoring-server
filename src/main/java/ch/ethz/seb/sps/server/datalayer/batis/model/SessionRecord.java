package ch.ethz.seb.sps.server.datalayer.batis.model;

import javax.annotation.Generated;

public class SessionRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.407+02:00", comments="Source field: session.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.407+02:00", comments="Source field: session.group_id")
    private Long groupId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.407+02:00", comments="Source field: session.uuid")
    private String uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.407+02:00", comments="Source field: session.image_format")
    private Integer imageFormat;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.407+02:00", comments="Source field: session.client_name")
    private String clientName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.407+02:00", comments="Source field: session.client_ip")
    private String clientIp;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.407+02:00", comments="Source field: session.client_machine_name")
    private String clientMachineName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.408+02:00", comments="Source field: session.client_os_name")
    private String clientOsName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.408+02:00", comments="Source field: session.client_version")
    private String clientVersion;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.408+02:00", comments="Source field: session.creation_time")
    private Long creationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.409+02:00", comments="Source field: session.last_update_time")
    private Long lastUpdateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.409+02:00", comments="Source field: session.termination_time")
    private Long terminationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.407+02:00", comments="Source Table: session")
    public SessionRecord(Long id, Long groupId, String uuid, Integer imageFormat, String clientName, String clientIp, String clientMachineName, String clientOsName, String clientVersion, Long creationTime, Long lastUpdateTime, Long terminationTime) {
        this.id = id;
        this.groupId = groupId;
        this.uuid = uuid;
        this.imageFormat = imageFormat;
        this.clientName = clientName;
        this.clientIp = clientIp;
        this.clientMachineName = clientMachineName;
        this.clientOsName = clientOsName;
        this.clientVersion = clientVersion;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.407+02:00", comments="Source field: session.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.407+02:00", comments="Source field: session.group_id")
    public Long getGroupId() {
        return groupId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.407+02:00", comments="Source field: session.uuid")
    public String getUuid() {
        return uuid;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.407+02:00", comments="Source field: session.image_format")
    public Integer getImageFormat() {
        return imageFormat;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.407+02:00", comments="Source field: session.client_name")
    public String getClientName() {
        return clientName;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.407+02:00", comments="Source field: session.client_ip")
    public String getClientIp() {
        return clientIp;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.408+02:00", comments="Source field: session.client_machine_name")
    public String getClientMachineName() {
        return clientMachineName;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.408+02:00", comments="Source field: session.client_os_name")
    public String getClientOsName() {
        return clientOsName;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.408+02:00", comments="Source field: session.client_version")
    public String getClientVersion() {
        return clientVersion;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.408+02:00", comments="Source field: session.creation_time")
    public Long getCreationTime() {
        return creationTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.409+02:00", comments="Source field: session.last_update_time")
    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.409+02:00", comments="Source field: session.termination_time")
    public Long getTerminationTime() {
        return terminationTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table session
     *
     * @mbg.generated Tue Sep 03 11:11:55 CEST 2024
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
        sb.append(", imageFormat=").append(imageFormat);
        sb.append(", clientName=").append(clientName);
        sb.append(", clientIp=").append(clientIp);
        sb.append(", clientMachineName=").append(clientMachineName);
        sb.append(", clientOsName=").append(clientOsName);
        sb.append(", clientVersion=").append(clientVersion);
        sb.append(", creationTime=").append(creationTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append(", terminationTime=").append(terminationTime);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table session
     *
     * @mbg.generated Tue Sep 03 11:11:55 CEST 2024
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
            && (this.getImageFormat() == null ? other.getImageFormat() == null : this.getImageFormat().equals(other.getImageFormat()))
            && (this.getClientName() == null ? other.getClientName() == null : this.getClientName().equals(other.getClientName()))
            && (this.getClientIp() == null ? other.getClientIp() == null : this.getClientIp().equals(other.getClientIp()))
            && (this.getClientMachineName() == null ? other.getClientMachineName() == null : this.getClientMachineName().equals(other.getClientMachineName()))
            && (this.getClientOsName() == null ? other.getClientOsName() == null : this.getClientOsName().equals(other.getClientOsName()))
            && (this.getClientVersion() == null ? other.getClientVersion() == null : this.getClientVersion().equals(other.getClientVersion()))
            && (this.getCreationTime() == null ? other.getCreationTime() == null : this.getCreationTime().equals(other.getCreationTime()))
            && (this.getLastUpdateTime() == null ? other.getLastUpdateTime() == null : this.getLastUpdateTime().equals(other.getLastUpdateTime()))
            && (this.getTerminationTime() == null ? other.getTerminationTime() == null : this.getTerminationTime().equals(other.getTerminationTime()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table session
     *
     * @mbg.generated Tue Sep 03 11:11:55 CEST 2024
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGroupId() == null) ? 0 : getGroupId().hashCode());
        result = prime * result + ((getUuid() == null) ? 0 : getUuid().hashCode());
        result = prime * result + ((getImageFormat() == null) ? 0 : getImageFormat().hashCode());
        result = prime * result + ((getClientName() == null) ? 0 : getClientName().hashCode());
        result = prime * result + ((getClientIp() == null) ? 0 : getClientIp().hashCode());
        result = prime * result + ((getClientMachineName() == null) ? 0 : getClientMachineName().hashCode());
        result = prime * result + ((getClientOsName() == null) ? 0 : getClientOsName().hashCode());
        result = prime * result + ((getClientVersion() == null) ? 0 : getClientVersion().hashCode());
        result = prime * result + ((getCreationTime() == null) ? 0 : getCreationTime().hashCode());
        result = prime * result + ((getLastUpdateTime() == null) ? 0 : getLastUpdateTime().hashCode());
        result = prime * result + ((getTerminationTime() == null) ? 0 : getTerminationTime().hashCode());
        return result;
    }
}