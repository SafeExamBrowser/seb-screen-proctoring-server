package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
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
public interface ScreenshotDataRecordMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.417+02:00", comments="Source Table: screenshot_data")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.417+02:00", comments="Source Table: screenshot_data")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.417+02:00", comments="Source Table: screenshot_data")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<ScreenshotDataRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.417+02:00", comments="Source Table: screenshot_data")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="session_uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="timestamp", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="image_format", javaType=Integer.class, jdbcType=JdbcType.INTEGER),
        @Arg(column="meta_data", javaType=String.class, jdbcType=JdbcType.VARCHAR)
    })
    ScreenshotDataRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.417+02:00", comments="Source Table: screenshot_data")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="session_uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="timestamp", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="image_format", javaType=Integer.class, jdbcType=JdbcType.INTEGER),
        @Arg(column="meta_data", javaType=String.class, jdbcType=JdbcType.VARCHAR)
    })
    List<ScreenshotDataRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.417+02:00", comments="Source Table: screenshot_data")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.417+02:00", comments="Source Table: screenshot_data")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(screenshotDataRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.417+02:00", comments="Source Table: screenshot_data")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, screenshotDataRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.417+02:00", comments="Source Table: screenshot_data")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, screenshotDataRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.417+02:00", comments="Source Table: screenshot_data")
    default int insert(ScreenshotDataRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(screenshotDataRecord)
                .map(sessionUuid).toProperty("sessionUuid")
                .map(timestamp).toProperty("timestamp")
                .map(imageFormat).toProperty("imageFormat")
                .map(metaData).toProperty("metaData")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.417+02:00", comments="Source Table: screenshot_data")
    default int insertSelective(ScreenshotDataRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(screenshotDataRecord)
                .map(sessionUuid).toPropertyWhenPresent("sessionUuid", record::getSessionUuid)
                .map(timestamp).toPropertyWhenPresent("timestamp", record::getTimestamp)
                .map(imageFormat).toPropertyWhenPresent("imageFormat", record::getImageFormat)
                .map(metaData).toPropertyWhenPresent("metaData", record::getMetaData)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.417+02:00", comments="Source Table: screenshot_data")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<ScreenshotDataRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, sessionUuid, timestamp, imageFormat, metaData)
                .from(screenshotDataRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.418+02:00", comments="Source Table: screenshot_data")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<ScreenshotDataRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, sessionUuid, timestamp, imageFormat, metaData)
                .from(screenshotDataRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.418+02:00", comments="Source Table: screenshot_data")
    default ScreenshotDataRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, sessionUuid, timestamp, imageFormat, metaData)
                .from(screenshotDataRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.418+02:00", comments="Source Table: screenshot_data")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(ScreenshotDataRecord record) {
        return UpdateDSL.updateWithMapper(this::update, screenshotDataRecord)
                .set(sessionUuid).equalTo(record::getSessionUuid)
                .set(timestamp).equalTo(record::getTimestamp)
                .set(imageFormat).equalTo(record::getImageFormat)
                .set(metaData).equalTo(record::getMetaData);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.418+02:00", comments="Source Table: screenshot_data")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(ScreenshotDataRecord record) {
        return UpdateDSL.updateWithMapper(this::update, screenshotDataRecord)
                .set(sessionUuid).equalToWhenPresent(record::getSessionUuid)
                .set(timestamp).equalToWhenPresent(record::getTimestamp)
                .set(imageFormat).equalToWhenPresent(record::getImageFormat)
                .set(metaData).equalToWhenPresent(record::getMetaData);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.418+02:00", comments="Source Table: screenshot_data")
    default int updateByPrimaryKey(ScreenshotDataRecord record) {
        return UpdateDSL.updateWithMapper(this::update, screenshotDataRecord)
                .set(sessionUuid).equalTo(record::getSessionUuid)
                .set(timestamp).equalTo(record::getTimestamp)
                .set(imageFormat).equalTo(record::getImageFormat)
                .set(metaData).equalTo(record::getMetaData)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.418+02:00", comments="Source Table: screenshot_data")
    default int updateByPrimaryKeySelective(ScreenshotDataRecord record) {
        return UpdateDSL.updateWithMapper(this::update, screenshotDataRecord)
                .set(sessionUuid).equalToWhenPresent(record::getSessionUuid)
                .set(timestamp).equalToWhenPresent(record::getTimestamp)
                .set(imageFormat).equalToWhenPresent(record::getImageFormat)
                .set(metaData).equalToWhenPresent(record::getMetaData)
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
                        .from(screenshotDataRecord);
    }
}