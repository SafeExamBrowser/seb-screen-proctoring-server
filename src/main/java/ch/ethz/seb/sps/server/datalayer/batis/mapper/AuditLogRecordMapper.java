package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.AuditLogRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import ch.ethz.seb.sps.server.datalayer.batis.model.AuditLogRecord;
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
public interface AuditLogRecordMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<AuditLogRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="user_uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="timestamp", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="activity_type", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="entity_type", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="entity_id", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="message", javaType=String.class, jdbcType=JdbcType.VARCHAR)
    })
    AuditLogRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="user_uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="timestamp", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="activity_type", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="entity_type", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="entity_id", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="message", javaType=String.class, jdbcType=JdbcType.VARCHAR)
    })
    List<AuditLogRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(auditLogRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, auditLogRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, auditLogRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    default int insert(AuditLogRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(auditLogRecord)
                .map(userUuid).toProperty("userUuid")
                .map(timestamp).toProperty("timestamp")
                .map(activityType).toProperty("activityType")
                .map(entityType).toProperty("entityType")
                .map(entityId).toProperty("entityId")
                .map(message).toProperty("message")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    default int insertSelective(AuditLogRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(auditLogRecord)
                .map(userUuid).toPropertyWhenPresent("userUuid", record::getUserUuid)
                .map(timestamp).toPropertyWhenPresent("timestamp", record::getTimestamp)
                .map(activityType).toPropertyWhenPresent("activityType", record::getActivityType)
                .map(entityType).toPropertyWhenPresent("entityType", record::getEntityType)
                .map(entityId).toPropertyWhenPresent("entityId", record::getEntityId)
                .map(message).toPropertyWhenPresent("message", record::getMessage)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<AuditLogRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, userUuid, timestamp, activityType, entityType, entityId, message)
                .from(auditLogRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<AuditLogRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, userUuid, timestamp, activityType, entityType, entityId, message)
                .from(auditLogRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    default AuditLogRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, userUuid, timestamp, activityType, entityType, entityId, message)
                .from(auditLogRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(AuditLogRecord record) {
        return UpdateDSL.updateWithMapper(this::update, auditLogRecord)
                .set(userUuid).equalTo(record::getUserUuid)
                .set(timestamp).equalTo(record::getTimestamp)
                .set(activityType).equalTo(record::getActivityType)
                .set(entityType).equalTo(record::getEntityType)
                .set(entityId).equalTo(record::getEntityId)
                .set(message).equalTo(record::getMessage);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(AuditLogRecord record) {
        return UpdateDSL.updateWithMapper(this::update, auditLogRecord)
                .set(userUuid).equalToWhenPresent(record::getUserUuid)
                .set(timestamp).equalToWhenPresent(record::getTimestamp)
                .set(activityType).equalToWhenPresent(record::getActivityType)
                .set(entityType).equalToWhenPresent(record::getEntityType)
                .set(entityId).equalToWhenPresent(record::getEntityId)
                .set(message).equalToWhenPresent(record::getMessage);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    default int updateByPrimaryKey(AuditLogRecord record) {
        return UpdateDSL.updateWithMapper(this::update, auditLogRecord)
                .set(userUuid).equalTo(record::getUserUuid)
                .set(timestamp).equalTo(record::getTimestamp)
                .set(activityType).equalTo(record::getActivityType)
                .set(entityType).equalTo(record::getEntityType)
                .set(entityId).equalTo(record::getEntityId)
                .set(message).equalTo(record::getMessage)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-08-26T16:02:14.762+02:00", comments="Source Table: audit_log")
    default int updateByPrimaryKeySelective(AuditLogRecord record) {
        return UpdateDSL.updateWithMapper(this::update, auditLogRecord)
                .set(userUuid).equalToWhenPresent(record::getUserUuid)
                .set(timestamp).equalToWhenPresent(record::getTimestamp)
                .set(activityType).equalToWhenPresent(record::getActivityType)
                .set(entityType).equalToWhenPresent(record::getEntityType)
                .set(entityId).equalToWhenPresent(record::getEntityId)
                .set(message).equalToWhenPresent(record::getMessage)
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
                        .from(auditLogRecord);
    }
}