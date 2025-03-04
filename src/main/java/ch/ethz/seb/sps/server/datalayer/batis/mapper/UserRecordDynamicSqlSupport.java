package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import jakarta.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class UserRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.350+01:00", comments="Source Table: user")
    public static final UserRecord userRecord = new UserRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.351+01:00", comments="Source field: user.id")
    public static final SqlColumn<Long> id = userRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.352+01:00", comments="Source field: user.uuid")
    public static final SqlColumn<String> uuid = userRecord.uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.352+01:00", comments="Source field: user.name")
    public static final SqlColumn<String> name = userRecord.name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.352+01:00", comments="Source field: user.surname")
    public static final SqlColumn<String> surname = userRecord.surname;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.352+01:00", comments="Source field: user.username")
    public static final SqlColumn<String> username = userRecord.username;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.352+01:00", comments="Source field: user.password")
    public static final SqlColumn<String> password = userRecord.password;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.352+01:00", comments="Source field: user.email")
    public static final SqlColumn<String> email = userRecord.email;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.352+01:00", comments="Source field: user.language")
    public static final SqlColumn<String> language = userRecord.language;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.352+01:00", comments="Source field: user.timezone")
    public static final SqlColumn<String> timezone = userRecord.timezone;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.352+01:00", comments="Source field: user.roles")
    public static final SqlColumn<String> roles = userRecord.roles;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.352+01:00", comments="Source field: user.creation_time")
    public static final SqlColumn<Long> creationTime = userRecord.creationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.353+01:00", comments="Source field: user.last_update_time")
    public static final SqlColumn<Long> lastUpdateTime = userRecord.lastUpdateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.353+01:00", comments="Source field: user.termination_time")
    public static final SqlColumn<Long> terminationTime = userRecord.terminationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.351+01:00", comments="Source Table: user")
    public static final class UserRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> uuid = column("uuid", JDBCType.VARCHAR);

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR);

        public final SqlColumn<String> surname = column("surname", JDBCType.VARCHAR);

        public final SqlColumn<String> username = column("username", JDBCType.VARCHAR);

        public final SqlColumn<String> password = column("password", JDBCType.VARCHAR);

        public final SqlColumn<String> email = column("email", JDBCType.VARCHAR);

        public final SqlColumn<String> language = column("language", JDBCType.VARCHAR);

        public final SqlColumn<String> timezone = column("timezone", JDBCType.VARCHAR);

        public final SqlColumn<String> roles = column("roles", JDBCType.VARCHAR);

        public final SqlColumn<Long> creationTime = column("creation_time", JDBCType.BIGINT);

        public final SqlColumn<Long> lastUpdateTime = column("last_update_time", JDBCType.BIGINT);

        public final SqlColumn<Long> terminationTime = column("termination_time", JDBCType.BIGINT);

        public UserRecord() {
            super("user");
        }
    }
}