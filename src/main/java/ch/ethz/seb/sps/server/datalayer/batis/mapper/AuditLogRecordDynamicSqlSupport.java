package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class AuditLogRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.432+02:00", comments="Source Table: audit_log")
    public static final AuditLogRecord auditLogRecord = new AuditLogRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.433+02:00", comments="Source field: audit_log.id")
    public static final SqlColumn<Long> id = auditLogRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.433+02:00", comments="Source field: audit_log.user_uuid")
    public static final SqlColumn<String> userUuid = auditLogRecord.userUuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.433+02:00", comments="Source field: audit_log.timestamp")
    public static final SqlColumn<Long> timestamp = auditLogRecord.timestamp;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.433+02:00", comments="Source field: audit_log.activity_type")
    public static final SqlColumn<String> activityType = auditLogRecord.activityType;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.433+02:00", comments="Source field: audit_log.entity_type")
    public static final SqlColumn<String> entityType = auditLogRecord.entityType;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.433+02:00", comments="Source field: audit_log.entity_id")
    public static final SqlColumn<Long> entityId = auditLogRecord.entityId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.433+02:00", comments="Source field: audit_log.message")
    public static final SqlColumn<String> message = auditLogRecord.message;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.433+02:00", comments="Source Table: audit_log")
    public static final class AuditLogRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> userUuid = column("user_uuid", JDBCType.VARCHAR);

        public final SqlColumn<Long> timestamp = column("timestamp", JDBCType.BIGINT);

        public final SqlColumn<String> activityType = column("activity_type", JDBCType.VARCHAR);

        public final SqlColumn<String> entityType = column("entity_type", JDBCType.VARCHAR);

        public final SqlColumn<Long> entityId = column("entity_id", JDBCType.BIGINT);

        public final SqlColumn<String> message = column("message", JDBCType.VARCHAR);

        public AuditLogRecord() {
            super("audit_log");
        }
    }
}