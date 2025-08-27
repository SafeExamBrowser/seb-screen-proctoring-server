package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataLiveCacheRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataLiveCacheRecord;
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
public interface ScreenshotDataLiveCacheRecordMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.757+02:00", comments="Source Table: screenshot_data_live_cache")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.757+02:00", comments="Source Table: screenshot_data_live_cache")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.757+02:00", comments="Source Table: screenshot_data_live_cache")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<ScreenshotDataLiveCacheRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.757+02:00", comments="Source Table: screenshot_data_live_cache")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="session_uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="id_latest_ssd", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    ScreenshotDataLiveCacheRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.757+02:00", comments="Source Table: screenshot_data_live_cache")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="session_uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="id_latest_ssd", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    List<ScreenshotDataLiveCacheRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.757+02:00", comments="Source Table: screenshot_data_live_cache")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source Table: screenshot_data_live_cache")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(screenshotDataLiveCacheRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source Table: screenshot_data_live_cache")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, screenshotDataLiveCacheRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source Table: screenshot_data_live_cache")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, screenshotDataLiveCacheRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source Table: screenshot_data_live_cache")
    default int insert(ScreenshotDataLiveCacheRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(screenshotDataLiveCacheRecord)
                .map(sessionUuid).toProperty("sessionUuid")
                .map(idLatestSsd).toProperty("idLatestSsd")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source Table: screenshot_data_live_cache")
    default int insertSelective(ScreenshotDataLiveCacheRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(screenshotDataLiveCacheRecord)
                .map(sessionUuid).toPropertyWhenPresent("sessionUuid", record::getSessionUuid)
                .map(idLatestSsd).toPropertyWhenPresent("idLatestSsd", record::getIdLatestSsd)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source Table: screenshot_data_live_cache")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<ScreenshotDataLiveCacheRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, sessionUuid, idLatestSsd)
                .from(screenshotDataLiveCacheRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source Table: screenshot_data_live_cache")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<ScreenshotDataLiveCacheRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, sessionUuid, idLatestSsd)
                .from(screenshotDataLiveCacheRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source Table: screenshot_data_live_cache")
    default ScreenshotDataLiveCacheRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, sessionUuid, idLatestSsd)
                .from(screenshotDataLiveCacheRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source Table: screenshot_data_live_cache")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(ScreenshotDataLiveCacheRecord record) {
        return UpdateDSL.updateWithMapper(this::update, screenshotDataLiveCacheRecord)
                .set(sessionUuid).equalTo(record::getSessionUuid)
                .set(idLatestSsd).equalTo(record::getIdLatestSsd);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source Table: screenshot_data_live_cache")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(ScreenshotDataLiveCacheRecord record) {
        return UpdateDSL.updateWithMapper(this::update, screenshotDataLiveCacheRecord)
                .set(sessionUuid).equalToWhenPresent(record::getSessionUuid)
                .set(idLatestSsd).equalToWhenPresent(record::getIdLatestSsd);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source Table: screenshot_data_live_cache")
    default int updateByPrimaryKey(ScreenshotDataLiveCacheRecord record) {
        return UpdateDSL.updateWithMapper(this::update, screenshotDataLiveCacheRecord)
                .set(sessionUuid).equalTo(record::getSessionUuid)
                .set(idLatestSsd).equalTo(record::getIdLatestSsd)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.758+02:00", comments="Source Table: screenshot_data_live_cache")
    default int updateByPrimaryKeySelective(ScreenshotDataLiveCacheRecord record) {
        return UpdateDSL.updateWithMapper(this::update, screenshotDataLiveCacheRecord)
                .set(sessionUuid).equalToWhenPresent(record::getSessionUuid)
                .set(idLatestSsd).equalToWhenPresent(record::getIdLatestSsd)
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
                        .from(screenshotDataLiveCacheRecord);
    }
}