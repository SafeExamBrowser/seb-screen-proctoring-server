package ch.ethz.seb.sps.server.datalayer.batis.model;

import javax.annotation.Generated;

public class ClientAccessRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.596+02:00", comments="Source field: client_access.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.596+02:00", comments="Source field: client_access.uuid")
    private String uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.597+02:00", comments="Source field: client_access.name")
    private String name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.597+02:00", comments="Source field: client_access.description")
    private String description;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.597+02:00", comments="Source field: client_access.client_name")
    private String clientName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.597+02:00", comments="Source field: client_access.client_secret")
    private String clientSecret;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.598+02:00", comments="Source field: client_access.owner")
    private String owner;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.598+02:00", comments="Source field: client_access.creation_time")
    private Long creationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.599+02:00", comments="Source field: client_access.last_update_time")
    private Long lastUpdateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.600+02:00", comments="Source field: client_access.termination_time")
    private Long terminationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.596+02:00", comments="Source Table: client_access")
    public ClientAccessRecord(Long id, String uuid, String name, String description, String clientName, String clientSecret, String owner, Long creationTime, Long lastUpdateTime, Long terminationTime) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.clientName = clientName;
        this.clientSecret = clientSecret;
        this.owner = owner;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.596+02:00", comments="Source field: client_access.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.597+02:00", comments="Source field: client_access.uuid")
    public String getUuid() {
        return uuid;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.597+02:00", comments="Source field: client_access.name")
    public String getName() {
        return name;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.597+02:00", comments="Source field: client_access.description")
    public String getDescription() {
        return description;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.597+02:00", comments="Source field: client_access.client_name")
    public String getClientName() {
        return clientName;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.597+02:00", comments="Source field: client_access.client_secret")
    public String getClientSecret() {
        return clientSecret;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.598+02:00", comments="Source field: client_access.owner")
    public String getOwner() {
        return owner;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.599+02:00", comments="Source field: client_access.creation_time")
    public Long getCreationTime() {
        return creationTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.600+02:00", comments="Source field: client_access.last_update_time")
    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-16T13:34:04.602+02:00", comments="Source field: client_access.termination_time")
    public Long getTerminationTime() {
        return terminationTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table client_access
     *
     * @mbg.generated Wed Aug 16 13:34:04 CEST 2023
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
        sb.append(", clientName=").append(clientName);
        sb.append(", clientSecret=").append(clientSecret);
        sb.append(", owner=").append(owner);
        sb.append(", creationTime=").append(creationTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append(", terminationTime=").append(terminationTime);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table client_access
     *
     * @mbg.generated Wed Aug 16 13:34:04 CEST 2023
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
        ClientAccessRecord other = (ClientAccessRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUuid() == null ? other.getUuid() == null : this.getUuid().equals(other.getUuid()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getClientName() == null ? other.getClientName() == null : this.getClientName().equals(other.getClientName()))
            && (this.getClientSecret() == null ? other.getClientSecret() == null : this.getClientSecret().equals(other.getClientSecret()))
            && (this.getOwner() == null ? other.getOwner() == null : this.getOwner().equals(other.getOwner()))
            && (this.getCreationTime() == null ? other.getCreationTime() == null : this.getCreationTime().equals(other.getCreationTime()))
            && (this.getLastUpdateTime() == null ? other.getLastUpdateTime() == null : this.getLastUpdateTime().equals(other.getLastUpdateTime()))
            && (this.getTerminationTime() == null ? other.getTerminationTime() == null : this.getTerminationTime().equals(other.getTerminationTime()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table client_access
     *
     * @mbg.generated Wed Aug 16 13:34:04 CEST 2023
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUuid() == null) ? 0 : getUuid().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getClientName() == null) ? 0 : getClientName().hashCode());
        result = prime * result + ((getClientSecret() == null) ? 0 : getClientSecret().hashCode());
        result = prime * result + ((getOwner() == null) ? 0 : getOwner().hashCode());
        result = prime * result + ((getCreationTime() == null) ? 0 : getCreationTime().hashCode());
        result = prime * result + ((getLastUpdateTime() == null) ? 0 : getLastUpdateTime().hashCode());
        result = prime * result + ((getTerminationTime() == null) ? 0 : getTerminationTime().hashCode());
        return result;
    }
}