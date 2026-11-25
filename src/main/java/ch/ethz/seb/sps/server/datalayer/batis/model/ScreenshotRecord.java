package ch.ethz.seb.sps.server.datalayer.batis.model;

import java.util.Arrays;
import jakarta.annotation.Generated;

public class ScreenshotRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-11-25T14:07:02.1271196+01:00", comments="Source field: screenshot.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-11-25T14:07:02.1271196+01:00", comments="Source field: screenshot.image")
    private byte[] image;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-11-25T14:07:02.1271196+01:00", comments="Source Table: screenshot")
    public ScreenshotRecord(Long id, byte[] image) {
        this.id = id;
        this.image = image;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-11-25T14:07:02.1271196+01:00", comments="Source field: screenshot.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-11-25T14:07:02.1277043+01:00", comments="Source field: screenshot.image")
    public byte[] getImage() {
        return image;
    }

    @Override
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-11-25T14:07:02.1277043+01:00", comments="Source Table: screenshot")
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

    @Override
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-11-25T14:07:02.1277043+01:00", comments="Source Table: screenshot")
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

    @Override
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-11-25T14:07:02.1277043+01:00", comments="Source Table: screenshot")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + (Arrays.hashCode(getImage()));
        return result;
    }
}