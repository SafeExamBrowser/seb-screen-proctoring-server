package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import jakarta.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class ScreenshotRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source Table: screenshot")
    public static final ScreenshotRecord screenshotRecord = new ScreenshotRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source field: screenshot.id")
    public static final SqlColumn<Long> id = screenshotRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source field: screenshot.image")
    public static final SqlColumn<byte[]> image = screenshotRecord.image;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source Table: screenshot")
    public static final class ScreenshotRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<byte[]> image = column("image", JDBCType.BLOB);

        public ScreenshotRecord() {
            super("screenshot");
        }
    }
}