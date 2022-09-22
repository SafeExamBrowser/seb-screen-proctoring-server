package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class GroupRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.796+02:00", comments="Source Table: GROUP")
    public static final GroupRecord groupRecord = new GroupRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.797+02:00", comments="Source field: GROUP.ID")
    public static final SqlColumn<Long> id = groupRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.797+02:00", comments="Source field: GROUP.UUID")
    public static final SqlColumn<String> uuid = groupRecord.uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.797+02:00", comments="Source field: GROUP.NAME")
    public static final SqlColumn<String> name = groupRecord.name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-22T17:16:53.797+02:00", comments="Source Table: GROUP")
    public static final class GroupRecord extends SqlTable {
        public final SqlColumn<Long> id = column("ID", JDBCType.BIGINT);

        public final SqlColumn<String> uuid = column("UUID", JDBCType.VARCHAR);

        public final SqlColumn<String> name = column("NAME", JDBCType.VARCHAR);

        public GroupRecord() {
            super("GROUP");
        }
    }
}