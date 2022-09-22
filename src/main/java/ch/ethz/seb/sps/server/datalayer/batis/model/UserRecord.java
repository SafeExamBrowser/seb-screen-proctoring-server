package ch.ethz.seb.sps.server.datalayer.batis.model;

import javax.annotation.Generated;
import org.joda.time.DateTime;

public class UserRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.761+02:00", comments="Source field: USER.ID")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.761+02:00", comments="Source field: USER.UUID")
    private String uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.761+02:00", comments="Source field: USER.CREATION_DATE")
    private DateTime creationDate;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.761+02:00", comments="Source field: USER.NAME")
    private String name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.SURNAME")
    private String surname;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.USERNAME")
    private String username;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.PASSWORD")
    private String password;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.EMAIL")
    private String email;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.LANGUAGE")
    private String language;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.TIMEZONE")
    private String timezone;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.ACTIVE")
    private Integer active;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.ROLES")
    private String roles;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.752+02:00", comments="Source Table: USER")
    public UserRecord(Long id, String uuid, DateTime creationDate, String name, String surname, String username, String password, String email, String language, String timezone, Integer active, String roles) {
        this.id = id;
        this.uuid = uuid;
        this.creationDate = creationDate;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.email = email;
        this.language = language;
        this.timezone = timezone;
        this.active = active;
        this.roles = roles;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.761+02:00", comments="Source field: USER.ID")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.761+02:00", comments="Source field: USER.UUID")
    public String getUuid() {
        return uuid;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.761+02:00", comments="Source field: USER.CREATION_DATE")
    public DateTime getCreationDate() {
        return creationDate;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.NAME")
    public String getName() {
        return name;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.SURNAME")
    public String getSurname() {
        return surname;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.USERNAME")
    public String getUsername() {
        return username;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.PASSWORD")
    public String getPassword() {
        return password;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.EMAIL")
    public String getEmail() {
        return email;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.LANGUAGE")
    public String getLanguage() {
        return language;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.TIMEZONE")
    public String getTimezone() {
        return timezone;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.762+02:00", comments="Source field: USER.ACTIVE")
    public Integer getActive() {
        return active;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.763+02:00", comments="Source field: USER.ROLES")
    public String getRoles() {
        return roles;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table USER
     *
     * @mbg.generated Thu Sep 22 17:16:53 CEST 2022
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", uuid=").append(uuid);
        sb.append(", creationDate=").append(creationDate);
        sb.append(", name=").append(name);
        sb.append(", surname=").append(surname);
        sb.append(", username=").append(username);
        sb.append(", password=").append(password);
        sb.append(", email=").append(email);
        sb.append(", language=").append(language);
        sb.append(", timezone=").append(timezone);
        sb.append(", active=").append(active);
        sb.append(", roles=").append(roles);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table USER
     *
     * @mbg.generated Thu Sep 22 17:16:53 CEST 2022
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
            && (this.getCreationDate() == null ? other.getCreationDate() == null : this.getCreationDate().equals(other.getCreationDate()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getSurname() == null ? other.getSurname() == null : this.getSurname().equals(other.getSurname()))
            && (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
            && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
            && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
            && (this.getLanguage() == null ? other.getLanguage() == null : this.getLanguage().equals(other.getLanguage()))
            && (this.getTimezone() == null ? other.getTimezone() == null : this.getTimezone().equals(other.getTimezone()))
            && (this.getActive() == null ? other.getActive() == null : this.getActive().equals(other.getActive()))
            && (this.getRoles() == null ? other.getRoles() == null : this.getRoles().equals(other.getRoles()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table USER
     *
     * @mbg.generated Thu Sep 22 17:16:53 CEST 2022
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUuid() == null) ? 0 : getUuid().hashCode());
        result = prime * result + ((getCreationDate() == null) ? 0 : getCreationDate().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getSurname() == null) ? 0 : getSurname().hashCode());
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getLanguage() == null) ? 0 : getLanguage().hashCode());
        result = prime * result + ((getTimezone() == null) ? 0 : getTimezone().hashCode());
        result = prime * result + ((getActive() == null) ? 0 : getActive().hashCode());
        result = prime * result + ((getRoles() == null) ? 0 : getRoles().hashCode());
        return result;
    }
}