package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class SessionRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.594+02:00", comments="Source Table: SESSION")
    public static final SessionRecord sessionRecord = new SessionRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.594+02:00", comments="Source field: SESSION.ID")
    public static final SqlColumn<Long> id = sessionRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.594+02:00", comments="Source field: SESSION.GROUP_ID")
    public static final SqlColumn<Long> groupId = sessionRecord.groupId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.595+02:00", comments="Source field: SESSION.UUID")
    public static final SqlColumn<String> uuid = sessionRecord.uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.595+02:00", comments="Source field: SESSION.NAME")
    public static final SqlColumn<String> name = sessionRecord.name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.594+02:00", comments="Source Table: SESSION")
    public static final class SessionRecord extends SqlTable {
        public final SqlColumn<Long> id = column("ID", JDBCType.BIGINT);

        public final SqlColumn<Long> groupId = column("GROUP_ID", JDBCType.BIGINT);

        public final SqlColumn<String> uuid = column("UUID", JDBCType.VARCHAR);

        public final SqlColumn<String> name = column("NAME", JDBCType.VARCHAR);

        public SessionRecord() {
            super("SESSION");
        }
    }
}