package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class ScreenshotDataRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.526+02:00", comments="Source Table: screenshot_data")
    public static final ScreenshotDataRecord screenshotDataRecord = new ScreenshotDataRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.526+02:00", comments="Source field: screenshot_data.id")
    public static final SqlColumn<Long> id = screenshotDataRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.526+02:00", comments="Source field: screenshot_data.session_uuid")
    public static final SqlColumn<String> sessionUuid = screenshotDataRecord.sessionUuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.526+02:00", comments="Source field: screenshot_data.timestamp")
    public static final SqlColumn<Long> timestamp = screenshotDataRecord.timestamp;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.526+02:00", comments="Source field: screenshot_data.image_url")
    public static final SqlColumn<String> imageUrl = screenshotDataRecord.imageUrl;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.527+02:00", comments="Source field: screenshot_data.image_format")
    public static final SqlColumn<String> imageFormat = screenshotDataRecord.imageFormat;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.527+02:00", comments="Source field: screenshot_data.meta_data")
    public static final SqlColumn<String> metaData = screenshotDataRecord.metaData;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.526+02:00", comments="Source Table: screenshot_data")
    public static final class ScreenshotDataRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> sessionUuid = column("session_uuid", JDBCType.VARCHAR);

        public final SqlColumn<Long> timestamp = column("timestamp", JDBCType.BIGINT);

        public final SqlColumn<String> imageUrl = column("image_url", JDBCType.VARCHAR);

        public final SqlColumn<String> imageFormat = column("image_format", JDBCType.VARCHAR);

        public final SqlColumn<String> metaData = column("meta_data", JDBCType.VARCHAR);

        public ScreenshotDataRecord() {
            super("screenshot_data");
        }
    }
}