package ch.ethz.seb.sps.server.datalayer.batis.model;

import javax.annotation.Generated;

public class GroupRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.751+02:00", comments="Source field: GROUP.ID")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.751+02:00", comments="Source field: GROUP.UUID")
    private String uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.752+02:00", comments="Source field: GROUP.NAME")
    private String name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.751+02:00", comments="Source Table: GROUP")
    public GroupRecord(Long id, String uuid, String name) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.751+02:00", comments="Source field: GROUP.ID")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.751+02:00", comments="Source field: GROUP.UUID")
    public String getUuid() {
        return uuid;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.752+02:00", comments="Source field: GROUP.NAME")
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GROUP
     *
     * @mbg.generated Wed Sep 21 14:52:03 CEST 2022
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
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GROUP
     *
     * @mbg.generated Wed Sep 21 14:52:03 CEST 2022
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
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GROUP
     *
     * @mbg.generated Wed Sep 21 14:52:03 CEST 2022
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUuid() == null) ? 0 : getUuid().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        return result;
    }
}