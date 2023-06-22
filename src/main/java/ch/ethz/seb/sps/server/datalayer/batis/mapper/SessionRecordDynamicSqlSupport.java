package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class SessionRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-22T17:01:10.197+02:00", comments="Source Table: session")
    public static final SessionRecord sessionRecord = new SessionRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-22T17:01:10.197+02:00", comments="Source field: session.id")
    public static final SqlColumn<Long> id = sessionRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-22T17:01:10.197+02:00", comments="Source field: session.group_id")
    public static final SqlColumn<Long> groupId = sessionRecord.groupId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-22T17:01:10.197+02:00", comments="Source field: session.uuid")
    public static final SqlColumn<String> uuid = sessionRecord.uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-22T17:01:10.198+02:00", comments="Source field: session.image_format")
    public static final SqlColumn<Integer> imageFormat = sessionRecord.imageFormat;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-22T17:01:10.198+02:00", comments="Source field: session.client_name")
    public static final SqlColumn<String> clientName = sessionRecord.clientName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-22T17:01:10.198+02:00", comments="Source field: session.client_ip")
    public static final SqlColumn<String> clientIp = sessionRecord.clientIp;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-22T17:01:10.198+02:00", comments="Source field: session.client_machine_name")
    public static final SqlColumn<String> clientMachineName = sessionRecord.clientMachineName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-22T17:01:10.198+02:00", comments="Source field: session.client_os_name")
    public static final SqlColumn<String> clientOsName = sessionRecord.clientOsName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-22T17:01:10.202+02:00", comments="Source field: session.client_version")
    public static final SqlColumn<String> clientVersion = sessionRecord.clientVersion;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-22T17:01:10.202+02:00", comments="Source field: session.creation_time")
    public static final SqlColumn<Long> creationTime = sessionRecord.creationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-22T17:01:10.202+02:00", comments="Source field: session.last_update_time")
    public static final SqlColumn<Long> lastUpdateTime = sessionRecord.lastUpdateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-22T17:01:10.202+02:00", comments="Source field: session.termination_time")
    public static final SqlColumn<Long> terminationTime = sessionRecord.terminationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-22T17:01:10.197+02:00", comments="Source Table: session")
    public static final class SessionRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<Long> groupId = column("group_id", JDBCType.BIGINT);

        public final SqlColumn<String> uuid = column("uuid", JDBCType.VARCHAR);

        public final SqlColumn<Integer> imageFormat = column("image_format", JDBCType.INTEGER);

        public final SqlColumn<String> clientName = column("client_name", JDBCType.VARCHAR);

        public final SqlColumn<String> clientIp = column("client_ip", JDBCType.VARCHAR);

        public final SqlColumn<String> clientMachineName = column("client_machine_name", JDBCType.VARCHAR);

        public final SqlColumn<String> clientOsName = column("client_os_name", JDBCType.VARCHAR);

        public final SqlColumn<String> clientVersion = column("client_version", JDBCType.VARCHAR);

        public final SqlColumn<Long> creationTime = column("creation_time", JDBCType.BIGINT);

        public final SqlColumn<Long> lastUpdateTime = column("last_update_time", JDBCType.BIGINT);

        public final SqlColumn<Long> terminationTime = column("termination_time", JDBCType.BIGINT);

        public SessionRecord() {
            super("session");
        }
    }
}