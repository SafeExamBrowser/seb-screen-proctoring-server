package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class ExamRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.397+02:00", comments="Source Table: exam")
    public static final ExamRecord examRecord = new ExamRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.397+02:00", comments="Source field: exam.id")
    public static final SqlColumn<Long> id = examRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.397+02:00", comments="Source field: exam.uuid")
    public static final SqlColumn<String> uuid = examRecord.uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.397+02:00", comments="Source field: exam.name")
    public static final SqlColumn<String> name = examRecord.name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.397+02:00", comments="Source field: exam.description")
    public static final SqlColumn<String> description = examRecord.description;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.397+02:00", comments="Source field: exam.url")
    public static final SqlColumn<String> url = examRecord.url;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.397+02:00", comments="Source field: exam.type")
    public static final SqlColumn<String> type = examRecord.type;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.397+02:00", comments="Source field: exam.owner")
    public static final SqlColumn<String> owner = examRecord.owner;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.397+02:00", comments="Source field: exam.creation_time")
    public static final SqlColumn<Long> creationTime = examRecord.creationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.397+02:00", comments="Source field: exam.last_update_time")
    public static final SqlColumn<Long> lastUpdateTime = examRecord.lastUpdateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.398+02:00", comments="Source field: exam.termination_time")
    public static final SqlColumn<Long> terminationTime = examRecord.terminationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.398+02:00", comments="Source field: exam.start_time")
    public static final SqlColumn<Long> startTime = examRecord.startTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.398+02:00", comments="Source field: exam.end_time")
    public static final SqlColumn<Long> endTime = examRecord.endTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.397+02:00", comments="Source Table: exam")
    public static final class ExamRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> uuid = column("uuid", JDBCType.VARCHAR);

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR);

        public final SqlColumn<String> description = column("description", JDBCType.VARCHAR);

        public final SqlColumn<String> url = column("url", JDBCType.VARCHAR);

        public final SqlColumn<String> type = column("type", JDBCType.VARCHAR);

        public final SqlColumn<String> owner = column("owner", JDBCType.VARCHAR);

        public final SqlColumn<Long> creationTime = column("creation_time", JDBCType.BIGINT);

        public final SqlColumn<Long> lastUpdateTime = column("last_update_time", JDBCType.BIGINT);

        public final SqlColumn<Long> terminationTime = column("termination_time", JDBCType.BIGINT);

        public final SqlColumn<Long> startTime = column("start_time", JDBCType.BIGINT);

        public final SqlColumn<Long> endTime = column("end_time", JDBCType.BIGINT);

        public ExamRecord() {
            super("exam");
        }
    }
}