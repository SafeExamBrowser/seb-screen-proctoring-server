package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import jakarta.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class ClientAccessRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.424+01:00", comments="Source Table: client_access")
    public static final ClientAccessRecord clientAccessRecord = new ClientAccessRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.424+01:00", comments="Source field: client_access.id")
    public static final SqlColumn<Long> id = clientAccessRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.424+01:00", comments="Source field: client_access.uuid")
    public static final SqlColumn<String> uuid = clientAccessRecord.uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.424+01:00", comments="Source field: client_access.name")
    public static final SqlColumn<String> name = clientAccessRecord.name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.424+01:00", comments="Source field: client_access.description")
    public static final SqlColumn<String> description = clientAccessRecord.description;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.424+01:00", comments="Source field: client_access.client_name")
    public static final SqlColumn<String> clientName = clientAccessRecord.clientName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.424+01:00", comments="Source field: client_access.client_secret")
    public static final SqlColumn<String> clientSecret = clientAccessRecord.clientSecret;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.424+01:00", comments="Source field: client_access.owner")
    public static final SqlColumn<String> owner = clientAccessRecord.owner;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.424+01:00", comments="Source field: client_access.creation_time")
    public static final SqlColumn<Long> creationTime = clientAccessRecord.creationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.424+01:00", comments="Source field: client_access.last_update_time")
    public static final SqlColumn<Long> lastUpdateTime = clientAccessRecord.lastUpdateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.424+01:00", comments="Source field: client_access.termination_time")
    public static final SqlColumn<Long> terminationTime = clientAccessRecord.terminationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.424+01:00", comments="Source Table: client_access")
    public static final class ClientAccessRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> uuid = column("uuid", JDBCType.VARCHAR);

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR);

        public final SqlColumn<String> description = column("description", JDBCType.VARCHAR);

        public final SqlColumn<String> clientName = column("client_name", JDBCType.VARCHAR);

        public final SqlColumn<String> clientSecret = column("client_secret", JDBCType.VARCHAR);

        public final SqlColumn<String> owner = column("owner", JDBCType.VARCHAR);

        public final SqlColumn<Long> creationTime = column("creation_time", JDBCType.BIGINT);

        public final SqlColumn<Long> lastUpdateTime = column("last_update_time", JDBCType.BIGINT);

        public final SqlColumn<Long> terminationTime = column("termination_time", JDBCType.BIGINT);

        public ClientAccessRecord() {
            super("client_access");
        }
    }
}