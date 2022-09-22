package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class ScreenshotRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.806+02:00", comments="Source Table: SCREENSHOT")
    public static final ScreenshotRecord screenshotRecord = new ScreenshotRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.806+02:00", comments="Source field: SCREENSHOT.ID")
    public static final SqlColumn<Long> id = screenshotRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.807+02:00", comments="Source field: SCREENSHOT.IMAGE")
    public static final SqlColumn<byte[]> image = screenshotRecord.image;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.806+02:00", comments="Source Table: SCREENSHOT")
    public static final class ScreenshotRecord extends SqlTable {
        public final SqlColumn<Long> id = column("ID", JDBCType.BIGINT);

        public final SqlColumn<byte[]> image = column("IMAGE", JDBCType.BLOB);

        public ScreenshotRecord() {
            super("SCREENSHOT");
        }
    }
}