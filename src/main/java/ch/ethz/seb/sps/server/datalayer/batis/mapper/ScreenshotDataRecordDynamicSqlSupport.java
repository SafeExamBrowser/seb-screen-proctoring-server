package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import jakarta.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class ScreenshotDataRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.606+01:00", comments="Source Table: screenshot_data")
    public static final ScreenshotDataRecord screenshotDataRecord = new ScreenshotDataRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.606+01:00", comments="Source field: screenshot_data.id")
    public static final SqlColumn<Long> id = screenshotDataRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.606+01:00", comments="Source field: screenshot_data.session_uuid")
    public static final SqlColumn<String> sessionUuid = screenshotDataRecord.sessionUuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.606+01:00", comments="Source field: screenshot_data.timestamp")
    public static final SqlColumn<Long> timestamp = screenshotDataRecord.timestamp;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.606+01:00", comments="Source field: screenshot_data.image_format")
    public static final SqlColumn<Integer> imageFormat = screenshotDataRecord.imageFormat;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.606+01:00", comments="Source field: screenshot_data.meta_data")
    public static final SqlColumn<String> metaData = screenshotDataRecord.metaData;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.606+01:00", comments="Source Table: screenshot_data")
    public static final class ScreenshotDataRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> sessionUuid = column("session_uuid", JDBCType.VARCHAR);

        public final SqlColumn<Long> timestamp = column("timestamp", JDBCType.BIGINT);

        public final SqlColumn<Integer> imageFormat = column("image_format", JDBCType.INTEGER);

        public final SqlColumn<String> metaData = column("meta_data", JDBCType.VARCHAR);

        public ScreenshotDataRecord() {
            super("screenshot_data");
        }
    }
}