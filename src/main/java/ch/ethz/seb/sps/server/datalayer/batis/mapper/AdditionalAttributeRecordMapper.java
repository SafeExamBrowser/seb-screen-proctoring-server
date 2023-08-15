package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.AdditionalAttributeRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import ch.ethz.seb.sps.server.datalayer.batis.model.AdditionalAttributeRecord;
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
public interface AdditionalAttributeRecordMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.886+02:00", comments="Source Table: additional_attribute")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.886+02:00", comments="Source Table: additional_attribute")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.886+02:00", comments="Source Table: additional_attribute")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<AdditionalAttributeRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.887+02:00", comments="Source Table: additional_attribute")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="entity_type", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="entity_id", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="value", javaType=String.class, jdbcType=JdbcType.VARCHAR)
    })
    AdditionalAttributeRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.887+02:00", comments="Source Table: additional_attribute")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="entity_type", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="entity_id", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="value", javaType=String.class, jdbcType=JdbcType.VARCHAR)
    })
    List<AdditionalAttributeRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.887+02:00", comments="Source Table: additional_attribute")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.887+02:00", comments="Source Table: additional_attribute")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(additionalAttributeRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.887+02:00", comments="Source Table: additional_attribute")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, additionalAttributeRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.887+02:00", comments="Source Table: additional_attribute")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, additionalAttributeRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.887+02:00", comments="Source Table: additional_attribute")
    default int insert(AdditionalAttributeRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(additionalAttributeRecord)
                .map(entityType).toProperty("entityType")
                .map(entityId).toProperty("entityId")
                .map(name).toProperty("name")
                .map(value).toProperty("value")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.887+02:00", comments="Source Table: additional_attribute")
    default int insertSelective(AdditionalAttributeRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(additionalAttributeRecord)
                .map(entityType).toPropertyWhenPresent("entityType", record::getEntityType)
                .map(entityId).toPropertyWhenPresent("entityId", record::getEntityId)
                .map(name).toPropertyWhenPresent("name", record::getName)
                .map(value).toPropertyWhenPresent("value", record::getValue)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.887+02:00", comments="Source Table: additional_attribute")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<AdditionalAttributeRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, entityType, entityId, name, value)
                .from(additionalAttributeRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.887+02:00", comments="Source Table: additional_attribute")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<AdditionalAttributeRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, entityType, entityId, name, value)
                .from(additionalAttributeRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.887+02:00", comments="Source Table: additional_attribute")
    default AdditionalAttributeRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, entityType, entityId, name, value)
                .from(additionalAttributeRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.887+02:00", comments="Source Table: additional_attribute")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(AdditionalAttributeRecord record) {
        return UpdateDSL.updateWithMapper(this::update, additionalAttributeRecord)
                .set(entityType).equalTo(record::getEntityType)
                .set(entityId).equalTo(record::getEntityId)
                .set(name).equalTo(record::getName)
                .set(value).equalTo(record::getValue);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.887+02:00", comments="Source Table: additional_attribute")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(AdditionalAttributeRecord record) {
        return UpdateDSL.updateWithMapper(this::update, additionalAttributeRecord)
                .set(entityType).equalToWhenPresent(record::getEntityType)
                .set(entityId).equalToWhenPresent(record::getEntityId)
                .set(name).equalToWhenPresent(record::getName)
                .set(value).equalToWhenPresent(record::getValue);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.888+02:00", comments="Source Table: additional_attribute")
    default int updateByPrimaryKey(AdditionalAttributeRecord record) {
        return UpdateDSL.updateWithMapper(this::update, additionalAttributeRecord)
                .set(entityType).equalTo(record::getEntityType)
                .set(entityId).equalTo(record::getEntityId)
                .set(name).equalTo(record::getName)
                .set(value).equalTo(record::getValue)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.888+02:00", comments="Source Table: additional_attribute")
    default int updateByPrimaryKeySelective(AdditionalAttributeRecord record) {
        return UpdateDSL.updateWithMapper(this::update, additionalAttributeRecord)
                .set(entityType).equalToWhenPresent(record::getEntityType)
                .set(entityId).equalToWhenPresent(record::getEntityId)
                .set(name).equalToWhenPresent(record::getName)
                .set(value).equalToWhenPresent(record::getValue)
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
                        .from(additionalAttributeRecord);
    }
}