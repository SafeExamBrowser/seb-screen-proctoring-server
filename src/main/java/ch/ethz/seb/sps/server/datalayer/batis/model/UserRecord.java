package ch.ethz.seb.sps.server.datalayer.batis.model;

import jakarta.annotation.Generated;

public class UserRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.339+01:00", comments="Source field: user.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.340+01:00", comments="Source field: user.uuid")
    private String uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.name")
    private String name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.surname")
    private String surname;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.username")
    private String username;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.password")
    private String password;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.email")
    private String email;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.language")
    private String language;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.timezone")
    private String timezone;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.roles")
    private String roles;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.creation_time")
    private Long creationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.last_update_time")
    private Long lastUpdateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.342+01:00", comments="Source field: user.termination_time")
    private Long terminationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.312+01:00", comments="Source Table: user")
    public UserRecord(Long id, String uuid, String name, String surname, String username, String password, String email, String language, String timezone, String roles, Long creationTime, Long lastUpdateTime, Long terminationTime) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.email = email;
        this.language = language;
        this.timezone = timezone;
        this.roles = roles;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.340+01:00", comments="Source field: user.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.uuid")
    public String getUuid() {
        return uuid;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.name")
    public String getName() {
        return name;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.surname")
    public String getSurname() {
        return surname;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.username")
    public String getUsername() {
        return username;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.password")
    public String getPassword() {
        return password;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.email")
    public String getEmail() {
        return email;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.language")
    public String getLanguage() {
        return language;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.timezone")
    public String getTimezone() {
        return timezone;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.roles")
    public String getRoles() {
        return roles;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.creation_time")
    public Long getCreationTime() {
        return creationTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.341+01:00", comments="Source field: user.last_update_time")
    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.342+01:00", comments="Source field: user.termination_time")
    public Long getTerminationTime() {
        return terminationTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbg.generated Tue Mar 04 15:13:08 CET 2025
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
        sb.append(", surname=").append(surname);
        sb.append(", username=").append(username);
        sb.append(", password=").append(password);
        sb.append(", email=").append(email);
        sb.append(", language=").append(language);
        sb.append(", timezone=").append(timezone);
        sb.append(", roles=").append(roles);
        sb.append(", creationTime=").append(creationTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append(", terminationTime=").append(terminationTime);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbg.generated Tue Mar 04 15:13:08 CET 2025
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
        UserRecord other = (UserRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUuid() == null ? other.getUuid() == null : this.getUuid().equals(other.getUuid()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getSurname() == null ? other.getSurname() == null : this.getSurname().equals(other.getSurname()))
            && (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
            && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
            && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
            && (this.getLanguage() == null ? other.getLanguage() == null : this.getLanguage().equals(other.getLanguage()))
            && (this.getTimezone() == null ? other.getTimezone() == null : this.getTimezone().equals(other.getTimezone()))
            && (this.getRoles() == null ? other.getRoles() == null : this.getRoles().equals(other.getRoles()))
            && (this.getCreationTime() == null ? other.getCreationTime() == null : this.getCreationTime().equals(other.getCreationTime()))
            && (this.getLastUpdateTime() == null ? other.getLastUpdateTime() == null : this.getLastUpdateTime().equals(other.getLastUpdateTime()))
            && (this.getTerminationTime() == null ? other.getTerminationTime() == null : this.getTerminationTime().equals(other.getTerminationTime()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbg.generated Tue Mar 04 15:13:08 CET 2025
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUuid() == null) ? 0 : getUuid().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getSurname() == null) ? 0 : getSurname().hashCode());
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getLanguage() == null) ? 0 : getLanguage().hashCode());
        result = prime * result + ((getTimezone() == null) ? 0 : getTimezone().hashCode());
        result = prime * result + ((getRoles() == null) ? 0 : getRoles().hashCode());
        result = prime * result + ((getCreationTime() == null) ? 0 : getCreationTime().hashCode());
        result = prime * result + ((getLastUpdateTime() == null) ? 0 : getLastUpdateTime().hashCode());
        result = prime * result + ((getTerminationTime() == null) ? 0 : getTerminationTime().hashCode());
        return result;
    }
}