package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.joda.time.DateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class ClientAccessRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.586+02:00", comments="Source Table: CLIENT_ACCESS")
    public static final ClientAccessRecord clientAccessRecord = new ClientAccessRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.586+02:00", comments="Source field: CLIENT_ACCESS.ID")
    public static final SqlColumn<Long> id = clientAccessRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.586+02:00", comments="Source field: CLIENT_ACCESS.CLIENT_NAME")
    public static final SqlColumn<String> clientName = clientAccessRecord.clientName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.586+02:00", comments="Source field: CLIENT_ACCESS.CLIENT_SECRET")
    public static final SqlColumn<String> clientSecret = clientAccessRecord.clientSecret;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.586+02:00", comments="Source field: CLIENT_ACCESS.CREATION_DATE")
    public static final SqlColumn<DateTime> creationDate = clientAccessRecord.creationDate;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.587+02:00", comments="Source field: CLIENT_ACCESS.ACTIVE")
    public static final SqlColumn<Integer> active = clientAccessRecord.active;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.586+02:00", comments="Source Table: CLIENT_ACCESS")
    public static final class ClientAccessRecord extends SqlTable {
        public final SqlColumn<Long> id = column("ID", JDBCType.BIGINT);

        public final SqlColumn<String> clientName = column("CLIENT_NAME", JDBCType.VARCHAR);

        public final SqlColumn<String> clientSecret = column("CLIENT_SECRET", JDBCType.VARCHAR);

        public final SqlColumn<DateTime> creationDate = column("CREATION_DATE", JDBCType.TIMESTAMP, "ch.ethz.seb.sps.server.datalayer.batis.JodaTimeTypeResolver");

        public final SqlColumn<Integer> active = column("ACTIVE", JDBCType.INTEGER);

        public ClientAccessRecord() {
            super("CLIENT_ACCESS");
        }
    }
}