package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ScheduledDeleteRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import ch.ethz.seb.sps.server.datalayer.batis.model.ScheduledDeleteRecord;
import java.util.List;
import jakarta.annotation.Generated;
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
public interface ScheduledDeleteRecordMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.173+01:00", comments="Source Table: scheduled_delete")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.173+01:00", comments="Source Table: scheduled_delete")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.173+01:00", comments="Source Table: scheduled_delete")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<ScheduledDeleteRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.173+01:00", comments="Source Table: scheduled_delete")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="state", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="start_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="delete_due_time", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    ScheduledDeleteRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.173+01:00", comments="Source Table: scheduled_delete")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="state", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="start_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="delete_due_time", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    List<ScheduledDeleteRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.173+01:00", comments="Source Table: scheduled_delete")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.173+01:00", comments="Source Table: scheduled_delete")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(scheduledDeleteRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.173+01:00", comments="Source Table: scheduled_delete")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, scheduledDeleteRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.174+01:00", comments="Source Table: scheduled_delete")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, scheduledDeleteRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.174+01:00", comments="Source Table: scheduled_delete")
    default int insert(ScheduledDeleteRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(scheduledDeleteRecord)
                .map(state).toProperty("state")
                .map(startTime).toProperty("startTime")
                .map(deleteDueTime).toProperty("deleteDueTime")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.174+01:00", comments="Source Table: scheduled_delete")
    default int insertSelective(ScheduledDeleteRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(scheduledDeleteRecord)
                .map(state).toPropertyWhenPresent("state", record::getState)
                .map(startTime).toPropertyWhenPresent("startTime", record::getStartTime)
                .map(deleteDueTime).toPropertyWhenPresent("deleteDueTime", record::getDeleteDueTime)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.174+01:00", comments="Source Table: scheduled_delete")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<ScheduledDeleteRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, state, startTime, deleteDueTime)
                .from(scheduledDeleteRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.174+01:00", comments="Source Table: scheduled_delete")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<ScheduledDeleteRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, state, startTime, deleteDueTime)
                .from(scheduledDeleteRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.174+01:00", comments="Source Table: scheduled_delete")
    default ScheduledDeleteRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, state, startTime, deleteDueTime)
                .from(scheduledDeleteRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.174+01:00", comments="Source Table: scheduled_delete")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(ScheduledDeleteRecord record) {
        return UpdateDSL.updateWithMapper(this::update, scheduledDeleteRecord)
                .set(state).equalTo(record::getState)
                .set(startTime).equalTo(record::getStartTime)
                .set(deleteDueTime).equalTo(record::getDeleteDueTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.175+01:00", comments="Source Table: scheduled_delete")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(ScheduledDeleteRecord record) {
        return UpdateDSL.updateWithMapper(this::update, scheduledDeleteRecord)
                .set(state).equalToWhenPresent(record::getState)
                .set(startTime).equalToWhenPresent(record::getStartTime)
                .set(deleteDueTime).equalToWhenPresent(record::getDeleteDueTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.175+01:00", comments="Source Table: scheduled_delete")
    default int updateByPrimaryKey(ScheduledDeleteRecord record) {
        return UpdateDSL.updateWithMapper(this::update, scheduledDeleteRecord)
                .set(state).equalTo(record::getState)
                .set(startTime).equalTo(record::getStartTime)
                .set(deleteDueTime).equalTo(record::getDeleteDueTime)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-02-19T13:37:36.175+01:00", comments="Source Table: scheduled_delete")
    default int updateByPrimaryKeySelective(ScheduledDeleteRecord record) {
        return UpdateDSL.updateWithMapper(this::update, scheduledDeleteRecord)
                .set(state).equalToWhenPresent(record::getState)
                .set(startTime).equalToWhenPresent(record::getStartTime)
                .set(deleteDueTime).equalToWhenPresent(record::getDeleteDueTime)
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
                        .from(scheduledDeleteRecord);
    }
}