package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.EntityPrivilegRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import ch.ethz.seb.sps.server.datalayer.batis.model.EntityPrivilegRecord;
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
public interface EntityPrivilegRecordMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.820+02:00", comments="Source Table: entity_privileg")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.820+02:00", comments="Source Table: entity_privileg")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.820+02:00", comments="Source Table: entity_privileg")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<EntityPrivilegRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.820+02:00", comments="Source Table: entity_privileg")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="entity_type", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="entity_id", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="user_uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="privileges", javaType=String.class, jdbcType=JdbcType.VARCHAR)
    })
    EntityPrivilegRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.821+02:00", comments="Source Table: entity_privileg")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="entity_type", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="entity_id", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="user_uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="privileges", javaType=String.class, jdbcType=JdbcType.VARCHAR)
    })
    List<EntityPrivilegRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.821+02:00", comments="Source Table: entity_privileg")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.821+02:00", comments="Source Table: entity_privileg")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(entityPrivilegRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.821+02:00", comments="Source Table: entity_privileg")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, entityPrivilegRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.821+02:00", comments="Source Table: entity_privileg")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, entityPrivilegRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.821+02:00", comments="Source Table: entity_privileg")
    default int insert(EntityPrivilegRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(entityPrivilegRecord)
                .map(entityType).toProperty("entityType")
                .map(entityId).toProperty("entityId")
                .map(userUuid).toProperty("userUuid")
                .map(privileges).toProperty("privileges")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.821+02:00", comments="Source Table: entity_privileg")
    default int insertSelective(EntityPrivilegRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(entityPrivilegRecord)
                .map(entityType).toPropertyWhenPresent("entityType", record::getEntityType)
                .map(entityId).toPropertyWhenPresent("entityId", record::getEntityId)
                .map(userUuid).toPropertyWhenPresent("userUuid", record::getUserUuid)
                .map(privileges).toPropertyWhenPresent("privileges", record::getPrivileges)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.821+02:00", comments="Source Table: entity_privileg")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<EntityPrivilegRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, entityType, entityId, userUuid, privileges)
                .from(entityPrivilegRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.821+02:00", comments="Source Table: entity_privileg")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<EntityPrivilegRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, entityType, entityId, userUuid, privileges)
                .from(entityPrivilegRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.821+02:00", comments="Source Table: entity_privileg")
    default EntityPrivilegRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, entityType, entityId, userUuid, privileges)
                .from(entityPrivilegRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.821+02:00", comments="Source Table: entity_privileg")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(EntityPrivilegRecord record) {
        return UpdateDSL.updateWithMapper(this::update, entityPrivilegRecord)
                .set(entityType).equalTo(record::getEntityType)
                .set(entityId).equalTo(record::getEntityId)
                .set(userUuid).equalTo(record::getUserUuid)
                .set(privileges).equalTo(record::getPrivileges);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.821+02:00", comments="Source Table: entity_privileg")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(EntityPrivilegRecord record) {
        return UpdateDSL.updateWithMapper(this::update, entityPrivilegRecord)
                .set(entityType).equalToWhenPresent(record::getEntityType)
                .set(entityId).equalToWhenPresent(record::getEntityId)
                .set(userUuid).equalToWhenPresent(record::getUserUuid)
                .set(privileges).equalToWhenPresent(record::getPrivileges);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.821+02:00", comments="Source Table: entity_privileg")
    default int updateByPrimaryKey(EntityPrivilegRecord record) {
        return UpdateDSL.updateWithMapper(this::update, entityPrivilegRecord)
                .set(entityType).equalTo(record::getEntityType)
                .set(entityId).equalTo(record::getEntityId)
                .set(userUuid).equalTo(record::getUserUuid)
                .set(privileges).equalTo(record::getPrivileges)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-06-15T15:45:11.821+02:00", comments="Source Table: entity_privileg")
    default int updateByPrimaryKeySelective(EntityPrivilegRecord record) {
        return UpdateDSL.updateWithMapper(this::update, entityPrivilegRecord)
                .set(entityType).equalToWhenPresent(record::getEntityType)
                .set(entityId).equalToWhenPresent(record::getEntityId)
                .set(userUuid).equalToWhenPresent(record::getUserUuid)
                .set(privileges).equalToWhenPresent(record::getPrivileges)
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
                        .from(entityPrivilegRecord);
    }
}