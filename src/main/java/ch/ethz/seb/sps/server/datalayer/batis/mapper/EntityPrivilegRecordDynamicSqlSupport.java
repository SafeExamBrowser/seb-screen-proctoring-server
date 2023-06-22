package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class EntityPrivilegRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.820+02:00", comments="Source Table: entity_privileg")
    public static final EntityPrivilegRecord entityPrivilegRecord = new EntityPrivilegRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.820+02:00", comments="Source field: entity_privileg.id")
    public static final SqlColumn<Long> id = entityPrivilegRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.820+02:00", comments="Source field: entity_privileg.entity_type")
    public static final SqlColumn<String> entityType = entityPrivilegRecord.entityType;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.820+02:00", comments="Source field: entity_privileg.entity_id")
    public static final SqlColumn<Long> entityId = entityPrivilegRecord.entityId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.820+02:00", comments="Source field: entity_privileg.user_uuid")
    public static final SqlColumn<String> userUuid = entityPrivilegRecord.userUuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.820+02:00", comments="Source field: entity_privileg.privileges")
    public static final SqlColumn<String> privileges = entityPrivilegRecord.privileges;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.820+02:00", comments="Source Table: entity_privileg")
    public static final class EntityPrivilegRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> entityType = column("entity_type", JDBCType.VARCHAR);

        public final SqlColumn<Long> entityId = column("entity_id", JDBCType.BIGINT);

        public final SqlColumn<String> userUuid = column("user_uuid", JDBCType.VARCHAR);

        public final SqlColumn<String> privileges = column("privileges", JDBCType.VARCHAR);

        public EntityPrivilegRecord() {
            super("entity_privileg");
        }
    }
}