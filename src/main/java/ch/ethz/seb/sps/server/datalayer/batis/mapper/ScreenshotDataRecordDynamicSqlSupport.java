package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class ScreenshotDataRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.759+02:00", comments="Source Table: SCREENSHOT_DATA")
    public static final ScreenshotDataRecord screenshotDataRecord = new ScreenshotDataRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.759+02:00", comments="Source field: SCREENSHOT_DATA.ID")
    public static final SqlColumn<Long> id = screenshotDataRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.759+02:00", comments="Source field: SCREENSHOT_DATA.SCREENSHOT_ID")
    public static final SqlColumn<Long> screenshotId = screenshotDataRecord.screenshotId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.760+02:00", comments="Source field: SCREENSHOT_DATA.SESSION_ID")
    public static final SqlColumn<String> sessionId = screenshotDataRecord.sessionId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.760+02:00", comments="Source field: SCREENSHOT_DATA.GROUPID")
    public static final SqlColumn<Long> groupid = screenshotDataRecord.groupid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.760+02:00", comments="Source field: SCREENSHOT_DATA.TIMESTAMP")
    public static final SqlColumn<Long> timestamp = screenshotDataRecord.timestamp;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.760+02:00", comments="Source field: SCREENSHOT_DATA.IMAGE_URL")
    public static final SqlColumn<String> imageUrl = screenshotDataRecord.imageUrl;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.760+02:00", comments="Source field: SCREENSHOT_DATA.IMAGE_FORMAT")
    public static final SqlColumn<String> imageFormat = screenshotDataRecord.imageFormat;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.760+02:00", comments="Source field: SCREENSHOT_DATA.META_DATA")
    public static final SqlColumn<String> metaData = screenshotDataRecord.metaData;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-21T14:52:03.759+02:00", comments="Source Table: SCREENSHOT_DATA")
    public static final class ScreenshotDataRecord extends SqlTable {
        public final SqlColumn<Long> id = column("ID", JDBCType.BIGINT);

        public final SqlColumn<Long> screenshotId = column("SCREENSHOT_ID", JDBCType.BIGINT);

        public final SqlColumn<String> sessionId = column("SESSION_ID", JDBCType.VARCHAR);

        public final SqlColumn<Long> groupid = column("GROUPID", JDBCType.BIGINT);

        public final SqlColumn<Long> timestamp = column("TIMESTAMP", JDBCType.BIGINT);

        public final SqlColumn<String> imageUrl = column("IMAGE_URL", JDBCType.VARCHAR);

        public final SqlColumn<String> imageFormat = column("IMAGE_FORMAT", JDBCType.VARCHAR);

        public final SqlColumn<String> metaData = column("META_DATA", JDBCType.VARCHAR);

        public ScreenshotDataRecord() {
            super("SCREENSHOT_DATA");
        }
    }
}