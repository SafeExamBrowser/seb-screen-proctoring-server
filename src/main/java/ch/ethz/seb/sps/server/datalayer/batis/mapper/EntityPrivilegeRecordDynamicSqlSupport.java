package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class EntityPrivilegeRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.558+02:00", comments="Source Table: entity_privilege")
    public static final EntityPrivilegeRecord entityPrivilegeRecord = new EntityPrivilegeRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.558+02:00", comments="Source field: entity_privilege.id")
    public static final SqlColumn<Long> id = entityPrivilegeRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.559+02:00", comments="Source field: entity_privilege.entity_type")
    public static final SqlColumn<String> entityType = entityPrivilegeRecord.entityType;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.559+02:00", comments="Source field: entity_privilege.entity_id")
    public static final SqlColumn<Long> entityId = entityPrivilegeRecord.entityId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.559+02:00", comments="Source field: entity_privilege.user_uuid")
    public static final SqlColumn<String> userUuid = entityPrivilegeRecord.userUuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.559+02:00", comments="Source field: entity_privilege.privileges")
    public static final SqlColumn<String> privileges = entityPrivilegeRecord.privileges;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-05-14T14:28:54.558+02:00", comments="Source Table: entity_privilege")
    public static final class EntityPrivilegeRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> entityType = column("entity_type", JDBCType.VARCHAR);

        public final SqlColumn<Long> entityId = column("entity_id", JDBCType.BIGINT);

        public final SqlColumn<String> userUuid = column("user_uuid", JDBCType.VARCHAR);

        public final SqlColumn<String> privileges = column("privileges", JDBCType.VARCHAR);

        public EntityPrivilegeRecord() {
            super("entity_privilege");
        }
    }
}