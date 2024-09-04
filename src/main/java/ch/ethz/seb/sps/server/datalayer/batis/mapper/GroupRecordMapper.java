package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import ch.ethz.seb.sps.server.datalayer.batis.model.GroupRecord;
import java.util.List;
import javax.annotation.Generated;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.delete.DeleteDSL;
import org.mybatis.dynamic.sql.delete.MyBatis3DeleteModelAdapter;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategy;
import org.mybatis.dynamic.sql.select.MyBatis3SelectModelAdapter;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.MyBatis3UpdateModelAdapter;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

@Mapper
public interface GroupRecordMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.404+02:00", comments="Source Table: seb_group")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.404+02:00", comments="Source Table: seb_group")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.404+02:00", comments="Source Table: seb_group")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<GroupRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.404+02:00", comments="Source Table: seb_group")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="description", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="owner", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="creation_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="last_update_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="termination_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="exam_id", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    GroupRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.404+02:00", comments="Source Table: seb_group")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="description", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="owner", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="creation_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="last_update_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="termination_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="exam_id", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    List<GroupRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.404+02:00", comments="Source Table: seb_group")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.404+02:00", comments="Source Table: seb_group")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(groupRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.404+02:00", comments="Source Table: seb_group")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, groupRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.405+02:00", comments="Source Table: seb_group")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, groupRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.405+02:00", comments="Source Table: seb_group")
    default int insert(GroupRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(groupRecord)
                .map(uuid).toProperty("uuid")
                .map(name).toProperty("name")
                .map(description).toProperty("description")
                .map(owner).toProperty("owner")
                .map(creationTime).toProperty("creationTime")
                .map(lastUpdateTime).toProperty("lastUpdateTime")
                .map(terminationTime).toProperty("terminationTime")
                .map(examId).toProperty("examId")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.405+02:00", comments="Source Table: seb_group")
    default int insertSelective(GroupRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(groupRecord)
                .map(uuid).toPropertyWhenPresent("uuid", record::getUuid)
                .map(name).toPropertyWhenPresent("name", record::getName)
                .map(description).toPropertyWhenPresent("description", record::getDescription)
                .map(owner).toPropertyWhenPresent("owner", record::getOwner)
                .map(creationTime).toPropertyWhenPresent("creationTime", record::getCreationTime)
                .map(lastUpdateTime).toPropertyWhenPresent("lastUpdateTime", record::getLastUpdateTime)
                .map(terminationTime).toPropertyWhenPresent("terminationTime", record::getTerminationTime)
                .map(examId).toPropertyWhenPresent("examId", record::getExamId)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.405+02:00", comments="Source Table: seb_group")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<GroupRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, uuid, name, description, owner, creationTime, lastUpdateTime, terminationTime, examId)
                .from(groupRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.405+02:00", comments="Source Table: seb_group")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<GroupRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, uuid, name, description, owner, creationTime, lastUpdateTime, terminationTime, examId)
                .from(groupRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.405+02:00", comments="Source Table: seb_group")
    default GroupRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, uuid, name, description, owner, creationTime, lastUpdateTime, terminationTime, examId)
                .from(groupRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.405+02:00", comments="Source Table: seb_group")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(GroupRecord record) {
        return UpdateDSL.updateWithMapper(this::update, groupRecord)
                .set(uuid).equalTo(record::getUuid)
                .set(name).equalTo(record::getName)
                .set(description).equalTo(record::getDescription)
                .set(owner).equalTo(record::getOwner)
                .set(creationTime).equalTo(record::getCreationTime)
                .set(lastUpdateTime).equalTo(record::getLastUpdateTime)
                .set(terminationTime).equalTo(record::getTerminationTime)
                .set(examId).equalTo(record::getExamId);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.405+02:00", comments="Source Table: seb_group")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(GroupRecord record) {
        return UpdateDSL.updateWithMapper(this::update, groupRecord)
                .set(uuid).equalToWhenPresent(record::getUuid)
                .set(name).equalToWhenPresent(record::getName)
                .set(description).equalToWhenPresent(record::getDescription)
                .set(owner).equalToWhenPresent(record::getOwner)
                .set(creationTime).equalToWhenPresent(record::getCreationTime)
                .set(lastUpdateTime).equalToWhenPresent(record::getLastUpdateTime)
                .set(terminationTime).equalToWhenPresent(record::getTerminationTime)
                .set(examId).equalToWhenPresent(record::getExamId);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.405+02:00", comments="Source Table: seb_group")
    default int updateByPrimaryKey(GroupRecord record) {
        return UpdateDSL.updateWithMapper(this::update, groupRecord)
                .set(uuid).equalTo(record::getUuid)
                .set(name).equalTo(record::getName)
                .set(description).equalTo(record::getDescription)
                .set(owner).equalTo(record::getOwner)
                .set(creationTime).equalTo(record::getCreationTime)
                .set(lastUpdateTime).equalTo(record::getLastUpdateTime)
                .set(terminationTime).equalTo(record::getTerminationTime)
                .set(examId).equalTo(record::getExamId)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.405+02:00", comments="Source Table: seb_group")
    default int updateByPrimaryKeySelective(GroupRecord record) {
        return UpdateDSL.updateWithMapper(this::update, groupRecord)
                .set(uuid).equalToWhenPresent(record::getUuid)
                .set(name).equalToWhenPresent(record::getName)
                .set(description).equalToWhenPresent(record::getDescription)
                .set(owner).equalToWhenPresent(record::getOwner)
                .set(creationTime).equalToWhenPresent(record::getCreationTime)
                .set(lastUpdateTime).equalToWhenPresent(record::getLastUpdateTime)
                .set(terminationTime).equalToWhenPresent(record::getTerminationTime)
                .set(examId).equalToWhenPresent(record::getExamId)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({@Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true)})
    List<Long> selectIds(SelectStatementProvider select);

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<Long>>> selectIdsByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectIds, id)
                        .from(groupRecord);
    }
}