package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import javax.annotation.Generated;
import java.sql.JDBCType;

public final class GroupRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.328+02:00", comments="Source Table: seb_group")
    public static final GroupRecord groupRecord = new GroupRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.328+02:00", comments="Source field: seb_group.id")
    public static final SqlColumn<Long> id = groupRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.328+02:00", comments="Source field: seb_group.uuid")
    public static final SqlColumn<String> uuid = groupRecord.uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.328+02:00", comments="Source field: seb_group.name")
    public static final SqlColumn<String> name = groupRecord.name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.328+02:00", comments="Source field: seb_group.description")
    public static final SqlColumn<String> description = groupRecord.description;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.328+02:00", comments="Source field: seb_group.owner")
    public static final SqlColumn<String> owner = groupRecord.owner;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.328+02:00", comments="Source field: seb_group.creation_time")
    public static final SqlColumn<Long> creationTime = groupRecord.creationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.328+02:00", comments="Source field: seb_group.last_update_time")
    public static final SqlColumn<Long> lastUpdateTime = groupRecord.lastUpdateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.328+02:00", comments="Source field: seb_group.termination_time")
    public static final SqlColumn<Long> terminationTime = groupRecord.terminationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.328+02:00", comments="Source field: seb_group.exam_id")
    public static final SqlColumn<Long> examId = groupRecord.examId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.328+02:00", comments="Source Table: seb_group")
    public static final class GroupRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> uuid = column("uuid", JDBCType.VARCHAR);

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR);

        public final SqlColumn<String> description = column("description", JDBCType.VARCHAR);

        public final SqlColumn<String> owner = column("owner", JDBCType.VARCHAR);

        public final SqlColumn<Long> creationTime = column("creation_time", JDBCType.BIGINT);

        public final SqlColumn<Long> lastUpdateTime = column("last_update_time", JDBCType.BIGINT);

        public final SqlColumn<Long> terminationTime = column("termination_time", JDBCType.BIGINT);

        public final SqlColumn<Long> examId = column("exam_id", JDBCType.BIGINT);

        public GroupRecord() {
            super("seb_group");
        }
    }
}