package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import jakarta.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class ScheduledDeleteRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-03-02T11:38:43.827+01:00", comments="Source Table: scheduled_delete")
    public static final ScheduledDeleteRecord scheduledDeleteRecord = new ScheduledDeleteRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-03-02T11:38:43.827+01:00", comments="Source field: scheduled_delete.id")
    public static final SqlColumn<Long> id = scheduledDeleteRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-03-02T11:38:43.827+01:00", comments="Source field: scheduled_delete.state")
    public static final SqlColumn<String> state = scheduledDeleteRecord.state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-03-02T11:38:43.827+01:00", comments="Source field: scheduled_delete.delete_due_time")
    public static final SqlColumn<Long> deleteDueTime = scheduledDeleteRecord.deleteDueTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-03-02T11:38:43.827+01:00", comments="Source field: scheduled_delete.schedule_time")
    public static final SqlColumn<Long> scheduleTime = scheduledDeleteRecord.scheduleTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-03-02T11:38:43.827+01:00", comments="Source field: scheduled_delete.start_time")
    public static final SqlColumn<Long> startTime = scheduledDeleteRecord.startTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-03-02T11:38:43.827+01:00", comments="Source field: scheduled_delete.end_time")
    public static final SqlColumn<Long> endTime = scheduledDeleteRecord.endTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-03-02T11:38:43.827+01:00", comments="Source field: scheduled_delete.owner")
    public static final SqlColumn<String> owner = scheduledDeleteRecord.owner;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-03-02T11:38:43.827+01:00", comments="Source Table: scheduled_delete")
    public static final class ScheduledDeleteRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> state = column("state", JDBCType.VARCHAR);

        public final SqlColumn<Long> deleteDueTime = column("delete_due_time", JDBCType.BIGINT);

        public final SqlColumn<Long> scheduleTime = column("schedule_time", JDBCType.BIGINT);

        public final SqlColumn<Long> startTime = column("start_time", JDBCType.BIGINT);

        public final SqlColumn<Long> endTime = column("end_time", JDBCType.BIGINT);

        public final SqlColumn<String> owner = column("owner", JDBCType.VARCHAR);

        public ScheduledDeleteRecord() {
            super("scheduled_delete");
        }
    }
}