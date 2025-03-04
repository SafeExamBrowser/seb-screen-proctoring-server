package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotRecord;
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
public interface ScreenshotRecordMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<ScreenshotRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="image", javaType=byte[].class, jdbcType=JdbcType.BLOB)
    })
    ScreenshotRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="image", javaType=byte[].class, jdbcType=JdbcType.BLOB)
    })
    List<ScreenshotRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(screenshotRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, screenshotRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, screenshotRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    default int insert(ScreenshotRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(screenshotRecord)
                .map(image).toProperty("image")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    default int insertSelective(ScreenshotRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(screenshotRecord)
                .map(image).toPropertyWhenPresent("image", record::getImage)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<ScreenshotRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, image)
                .from(screenshotRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<ScreenshotRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, image)
                .from(screenshotRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    default ScreenshotRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, image)
                .from(screenshotRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(ScreenshotRecord record) {
        return UpdateDSL.updateWithMapper(this::update, screenshotRecord)
                .set(image).equalTo(record::getImage);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(ScreenshotRecord record) {
        return UpdateDSL.updateWithMapper(this::update, screenshotRecord)
                .set(image).equalToWhenPresent(record::getImage);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    default int updateByPrimaryKey(ScreenshotRecord record) {
        return UpdateDSL.updateWithMapper(this::update, screenshotRecord)
                .set(image).equalTo(record::getImage)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T15:13:08.440+01:00", comments="Source Table: screenshot")
    default int updateByPrimaryKeySelective(ScreenshotRecord record) {
        return UpdateDSL.updateWithMapper(this::update, screenshotRecord)
                .set(image).equalToWhenPresent(record::getImage)
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
                        .from(screenshotRecord);
    }
}