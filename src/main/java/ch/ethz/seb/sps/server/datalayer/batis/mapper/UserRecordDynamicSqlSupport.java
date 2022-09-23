package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.joda.time.DateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class UserRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.560+02:00", comments="Source Table: USER")
    public static final UserRecord userRecord = new UserRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.562+02:00", comments="Source field: USER.ID")
    public static final SqlColumn<Long> id = userRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.563+02:00", comments="Source field: USER.UUID")
    public static final SqlColumn<String> uuid = userRecord.uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.563+02:00", comments="Source field: USER.CREATION_DATE")
    public static final SqlColumn<DateTime> creationDate = userRecord.creationDate;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.563+02:00", comments="Source field: USER.NAME")
    public static final SqlColumn<String> name = userRecord.name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.563+02:00", comments="Source field: USER.SURNAME")
    public static final SqlColumn<String> surname = userRecord.surname;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.564+02:00", comments="Source field: USER.USERNAME")
    public static final SqlColumn<String> username = userRecord.username;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.564+02:00", comments="Source field: USER.PASSWORD")
    public static final SqlColumn<String> password = userRecord.password;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.564+02:00", comments="Source field: USER.EMAIL")
    public static final SqlColumn<String> email = userRecord.email;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.564+02:00", comments="Source field: USER.LANGUAGE")
    public static final SqlColumn<String> language = userRecord.language;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.564+02:00", comments="Source field: USER.TIMEZONE")
    public static final SqlColumn<String> timezone = userRecord.timezone;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.564+02:00", comments="Source field: USER.ACTIVE")
    public static final SqlColumn<Integer> active = userRecord.active;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.565+02:00", comments="Source field: USER.ROLES")
    public static final SqlColumn<String> roles = userRecord.roles;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.562+02:00", comments="Source Table: USER")
    public static final class UserRecord extends SqlTable {
        public final SqlColumn<Long> id = column("ID", JDBCType.BIGINT);

        public final SqlColumn<String> uuid = column("UUID", JDBCType.VARCHAR);

        public final SqlColumn<DateTime> creationDate = column("CREATION_DATE", JDBCType.TIMESTAMP, "ch.ethz.seb.sps.server.datalayer.batis.JodaTimeTypeResolver");

        public final SqlColumn<String> name = column("NAME", JDBCType.VARCHAR);

        public final SqlColumn<String> surname = column("SURNAME", JDBCType.VARCHAR);

        public final SqlColumn<String> username = column("USERNAME", JDBCType.VARCHAR);

        public final SqlColumn<String> password = column("PASSWORD", JDBCType.VARCHAR);

        public final SqlColumn<String> email = column("EMAIL", JDBCType.VARCHAR);

        public final SqlColumn<String> language = column("LANGUAGE", JDBCType.VARCHAR);

        public final SqlColumn<String> timezone = column("TIMEZONE", JDBCType.VARCHAR);

        public final SqlColumn<Integer> active = column("ACTIVE", JDBCType.INTEGER);

        public final SqlColumn<String> roles = column("ROLES", JDBCType.VARCHAR);

        public UserRecord() {
            super("USER");
        }
    }
}