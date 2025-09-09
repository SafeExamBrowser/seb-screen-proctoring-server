package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import jakarta.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class ScreenshotDataLiveCacheRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.757+02:00", comments="Source Table: screenshot_data_live_cache")
    public static final ScreenshotDataLiveCacheRecord screenshotDataLiveCacheRecord = new ScreenshotDataLiveCacheRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.757+02:00", comments="Source field: screenshot_data_live_cache.id")
    public static final SqlColumn<Long> id = screenshotDataLiveCacheRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.757+02:00", comments="Source field: screenshot_data_live_cache.session_uuid")
    public static final SqlColumn<String> sessionUuid = screenshotDataLiveCacheRecord.sessionUuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.757+02:00", comments="Source field: screenshot_data_live_cache.id_latest_ssd")
    public static final SqlColumn<Long> idLatestSsd = screenshotDataLiveCacheRecord.idLatestSsd;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.757+02:00", comments="Source Table: screenshot_data_live_cache")
    public static final class ScreenshotDataLiveCacheRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> sessionUuid = column("session_uuid", JDBCType.VARCHAR);

        public final SqlColumn<Long> idLatestSsd = column("id_latest_ssd", JDBCType.BIGINT);

        public ScreenshotDataLiveCacheRecord() {
            super("screenshot_data_live_cache");
        }
    }
}