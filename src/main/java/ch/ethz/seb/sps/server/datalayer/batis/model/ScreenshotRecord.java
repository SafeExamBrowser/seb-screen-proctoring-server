package ch.ethz.seb.sps.server.datalayer.batis.model;

import java.util.Arrays;
import javax.annotation.Generated;

public class ScreenshotRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.601+02:00", comments="Source field: SCREENSHOT.ID")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.601+02:00", comments="Source field: SCREENSHOT.IMAGE")
    private byte[] image;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.601+02:00", comments="Source Table: SCREENSHOT")
    public ScreenshotRecord(Long id, byte[] image) {
        this.id = id;
        this.image = image;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.601+02:00", comments="Source field: SCREENSHOT.ID")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.601+02:00", comments="Source field: SCREENSHOT.IMAGE")
    public byte[] getImage() {
        return image;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SCREENSHOT
     *
     * @mbg.generated Fri Sep 23 10:10:02 CEST 2022
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", image=").append(image);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SCREENSHOT
     *
     * @mbg.generated Fri Sep 23 10:10:02 CEST 2022
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
        ScreenshotRecord other = (ScreenshotRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (Arrays.equals(this.getImage(), other.getImage()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SCREENSHOT
     *
     * @mbg.generated Fri Sep 23 10:10:02 CEST 2022
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + (Arrays.hashCode(getImage()));
        return result;
    }
}