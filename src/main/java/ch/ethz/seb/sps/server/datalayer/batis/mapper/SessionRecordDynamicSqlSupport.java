package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class SessionRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.515+02:00", comments="Source Table: session")
    public static final SessionRecord sessionRecord = new SessionRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.516+02:00", comments="Source field: session.id")
    public static final SqlColumn<Long> id = sessionRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.516+02:00", comments="Source field: session.group_id")
    public static final SqlColumn<Long> groupId = sessionRecord.groupId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.516+02:00", comments="Source field: session.uuid")
    public static final SqlColumn<String> uuid = sessionRecord.uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.516+02:00", comments="Source field: session.name")
    public static final SqlColumn<String> name = sessionRecord.name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.516+02:00", comments="Source field: session.creation_time")
    public static final SqlColumn<Long> creationTime = sessionRecord.creationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.517+02:00", comments="Source field: session.termination_time")
    public static final SqlColumn<Long> terminationTime = sessionRecord.terminationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.515+02:00", comments="Source Table: session")
    public static final class SessionRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<Long> groupId = column("group_id", JDBCType.BIGINT);

        public final SqlColumn<String> uuid = column("uuid", JDBCType.VARCHAR);

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR);

        public final SqlColumn<Long> creationTime = column("creation_time", JDBCType.BIGINT);

        public final SqlColumn<Long> terminationTime = column("termination_time", JDBCType.BIGINT);

        public SessionRecord() {
            super("session");
        }
    }
}