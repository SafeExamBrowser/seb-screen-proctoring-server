package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.joda.time.DateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class ClientAccessRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.498+02:00", comments="Source Table: client_access")
    public static final ClientAccessRecord clientAccessRecord = new ClientAccessRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.498+02:00", comments="Source field: client_access.id")
    public static final SqlColumn<Long> id = clientAccessRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.498+02:00", comments="Source field: client_access.client_name")
    public static final SqlColumn<String> clientName = clientAccessRecord.clientName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.499+02:00", comments="Source field: client_access.client_secret")
    public static final SqlColumn<String> clientSecret = clientAccessRecord.clientSecret;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.499+02:00", comments="Source field: client_access.creation_date")
    public static final SqlColumn<DateTime> creationDate = clientAccessRecord.creationDate;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.499+02:00", comments="Source field: client_access.active")
    public static final SqlColumn<Integer> active = clientAccessRecord.active;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.498+02:00", comments="Source Table: client_access")
    public static final class ClientAccessRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> clientName = column("client_name", JDBCType.VARCHAR);

        public final SqlColumn<String> clientSecret = column("client_secret", JDBCType.VARCHAR);

        public final SqlColumn<DateTime> creationDate = column("creation_date", JDBCType.TIMESTAMP, "ch.ethz.seb.sps.server.datalayer.batis.JodaTimeTypeResolver");

        public final SqlColumn<Integer> active = column("active", JDBCType.INTEGER);

        public ClientAccessRecord() {
            super("client_access");
        }
    }
}