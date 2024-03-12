package ch.ethz.seb.sps.server.datalayer.batis.custommappers;

import ch.ethz.seb.sps.server.datalayer.batis.customrecords.GroupViewRecord;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ExamRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.select.MyBatis3SelectModelAdapter;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

import java.util.Collection;

@Mapper
public interface GroupViewMapper {

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class, jdbcType = JdbcType.BIGINT, id = true),
            @Arg(column = "uuid", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Arg(column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Arg(column = "description", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Arg(column = "owner", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Arg(column = "creation_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Arg(column = "last_update_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Arg(column = "termination_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),

            @Arg(column = "exam_uuid", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Arg(column = "exam_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Arg(column = "exam_start_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Arg(column = "exam_end_time", javaType = Long.class, jdbcType = JdbcType.BIGINT)
    })
    GroupViewRecord selectOne(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class, jdbcType = JdbcType.BIGINT, id = true),
            @Arg(column = "uuid", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Arg(column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Arg(column = "description", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Arg(column = "owner", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Arg(column = "creation_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Arg(column = "last_update_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Arg(column = "termination_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),

            @Arg(column = "exam_uuid", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Arg(column = "exam_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Arg(column = "exam_start_time", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Arg(column = "exam_end_time", javaType = Long.class, jdbcType = JdbcType.BIGINT)
    })
    Collection<GroupViewRecord> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class, jdbcType = JdbcType.BIGINT, id = true),
    })
    Collection<Long> selectIds(SelectStatementProvider selectStatement);

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<GroupViewRecord>>.JoinSpecificationFinisher getGroupWithExamData() {
        return SelectDSL.selectWithMapper(
                        this::selectOne,

                        GroupRecordDynamicSqlSupport.id,
                        GroupRecordDynamicSqlSupport.uuid,
                        GroupRecordDynamicSqlSupport.name,
                        GroupRecordDynamicSqlSupport.description,
                        GroupRecordDynamicSqlSupport.owner,
                        GroupRecordDynamicSqlSupport.creationTime,
                        GroupRecordDynamicSqlSupport.lastUpdateTime,
                        GroupRecordDynamicSqlSupport.terminationTime,

                        ExamRecordDynamicSqlSupport.uuid.as("exam_uuid"),
                        ExamRecordDynamicSqlSupport.name.as("exam_name"),
                        ExamRecordDynamicSqlSupport.startTime.as("exam_start_time"),
                        ExamRecordDynamicSqlSupport.endTime.as("exam_end_time"))

                .from(GroupRecordDynamicSqlSupport.groupRecord)
                .leftJoin(ExamRecordDynamicSqlSupport.examRecord)
                .on(
                        GroupRecordDynamicSqlSupport.examId,
                        SqlBuilder.equalTo(ExamRecordDynamicSqlSupport.id));
    }

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Collection<GroupViewRecord>>>.JoinSpecificationFinisher getGroupsWithExamData() {
        return SelectDSL.selectWithMapper(
                        this::selectMany,

                        GroupRecordDynamicSqlSupport.id,
                        GroupRecordDynamicSqlSupport.uuid,
                        GroupRecordDynamicSqlSupport.name,
                        GroupRecordDynamicSqlSupport.description,
                        GroupRecordDynamicSqlSupport.owner,
                        GroupRecordDynamicSqlSupport.creationTime,
                        GroupRecordDynamicSqlSupport.lastUpdateTime,
                        GroupRecordDynamicSqlSupport.terminationTime,

                        ExamRecordDynamicSqlSupport.uuid.as("exam_uuid"),
                        ExamRecordDynamicSqlSupport.name.as("exam_name"),
                        ExamRecordDynamicSqlSupport.startTime.as("exam_start_time"),
                        ExamRecordDynamicSqlSupport.endTime.as("exam_end_time"))

                .from(GroupRecordDynamicSqlSupport.groupRecord)
                .leftJoin(ExamRecordDynamicSqlSupport.examRecord)
                .on(
                        GroupRecordDynamicSqlSupport.examId,
                        SqlBuilder.equalTo(ExamRecordDynamicSqlSupport.id));
    }

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Collection<Long>>>.JoinSpecificationFinisher getGroupIdsWithExamData() {
        return SelectDSL.selectWithMapper(
                        this::selectIds,
                        GroupRecordDynamicSqlSupport.id)

                .from(GroupRecordDynamicSqlSupport.groupRecord)
                .leftJoin(ExamRecordDynamicSqlSupport.examRecord)
                .on(
                        GroupRecordDynamicSqlSupport.examId,
                        SqlBuilder.equalTo(ExamRecordDynamicSqlSupport.id));
    }

}
