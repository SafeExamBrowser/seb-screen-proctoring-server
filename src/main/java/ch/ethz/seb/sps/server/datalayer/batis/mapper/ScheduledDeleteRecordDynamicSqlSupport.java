package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import jakarta.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class ScheduledDeleteRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.173+01:00", comments="Source Table: scheduled_delete")
    public static final ScheduledDeleteRecord scheduledDeleteRecord = new ScheduledDeleteRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.173+01:00", comments="Source field: scheduled_delete.id")
    public static final SqlColumn<Long> id = scheduledDeleteRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.173+01:00", comments="Source field: scheduled_delete.state")
    public static final SqlColumn<String> state = scheduledDeleteRecord.state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.173+01:00", comments="Source field: scheduled_delete.start_time")
    public static final SqlColumn<Long> startTime = scheduledDeleteRecord.startTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.173+01:00", comments="Source field: scheduled_delete.delete_due_time")
    public static final SqlColumn<Long> deleteDueTime = scheduledDeleteRecord.deleteDueTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.173+01:00", comments="Source Table: scheduled_delete")
    public static final class ScheduledDeleteRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> state = column("state", JDBCType.VARCHAR);

        public final SqlColumn<Long> startTime = column("start_time", JDBCType.BIGINT);

        public final SqlColumn<Long> deleteDueTime = column("delete_due_time", JDBCType.BIGINT);

        public ScheduledDeleteRecord() {
            super("scheduled_delete");
        }
    }
}