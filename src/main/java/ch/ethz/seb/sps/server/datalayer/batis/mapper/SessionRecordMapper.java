package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.SessionRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import ch.ethz.seb.sps.server.datalayer.batis.model.SessionRecord;
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
public interface SessionRecordMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.871+02:00", comments="Source Table: session")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.871+02:00", comments="Source Table: session")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.871+02:00", comments="Source Table: session")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<SessionRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.871+02:00", comments="Source Table: session")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="group_id", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="image_format", javaType=Integer.class, jdbcType=JdbcType.INTEGER),
        @Arg(column="client_name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="client_ip", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="client_machine_name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="client_os_name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="client_version", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="creation_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="last_update_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="termination_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="first_screenshot_time", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    SessionRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.871+02:00", comments="Source Table: session")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="group_id", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="image_format", javaType=Integer.class, jdbcType=JdbcType.INTEGER),
        @Arg(column="client_name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="client_ip", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="client_machine_name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="client_os_name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="client_version", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="creation_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="last_update_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="termination_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="first_screenshot_time", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    List<SessionRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.871+02:00", comments="Source Table: session")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.872+02:00", comments="Source Table: session")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(sessionRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.872+02:00", comments="Source Table: session")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, sessionRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.872+02:00", comments="Source Table: session")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, sessionRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.872+02:00", comments="Source Table: session")
    default int insert(SessionRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(sessionRecord)
                .map(groupId).toProperty("groupId")
                .map(uuid).toProperty("uuid")
                .map(imageFormat).toProperty("imageFormat")
                .map(clientName).toProperty("clientName")
                .map(clientIp).toProperty("clientIp")
                .map(clientMachineName).toProperty("clientMachineName")
                .map(clientOsName).toProperty("clientOsName")
                .map(clientVersion).toProperty("clientVersion")
                .map(creationTime).toProperty("creationTime")
                .map(lastUpdateTime).toProperty("lastUpdateTime")
                .map(terminationTime).toProperty("terminationTime")
                .map(firstScreenshotTime).toProperty("firstScreenshotTime")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.872+02:00", comments="Source Table: session")
    default int insertSelective(SessionRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(sessionRecord)
                .map(groupId).toPropertyWhenPresent("groupId", record::getGroupId)
                .map(uuid).toPropertyWhenPresent("uuid", record::getUuid)
                .map(imageFormat).toPropertyWhenPresent("imageFormat", record::getImageFormat)
                .map(clientName).toPropertyWhenPresent("clientName", record::getClientName)
                .map(clientIp).toPropertyWhenPresent("clientIp", record::getClientIp)
                .map(clientMachineName).toPropertyWhenPresent("clientMachineName", record::getClientMachineName)
                .map(clientOsName).toPropertyWhenPresent("clientOsName", record::getClientOsName)
                .map(clientVersion).toPropertyWhenPresent("clientVersion", record::getClientVersion)
                .map(creationTime).toPropertyWhenPresent("creationTime", record::getCreationTime)
                .map(lastUpdateTime).toPropertyWhenPresent("lastUpdateTime", record::getLastUpdateTime)
                .map(terminationTime).toPropertyWhenPresent("terminationTime", record::getTerminationTime)
                .map(firstScreenshotTime).toPropertyWhenPresent("firstScreenshotTime", record::getFirstScreenshotTime)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.872+02:00", comments="Source Table: session")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<SessionRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, groupId, uuid, imageFormat, clientName, clientIp, clientMachineName, clientOsName, clientVersion, creationTime, lastUpdateTime, terminationTime, firstScreenshotTime)
                .from(sessionRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.872+02:00", comments="Source Table: session")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<SessionRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, groupId, uuid, imageFormat, clientName, clientIp, clientMachineName, clientOsName, clientVersion, creationTime, lastUpdateTime, terminationTime, firstScreenshotTime)
                .from(sessionRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.872+02:00", comments="Source Table: session")
    default SessionRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, groupId, uuid, imageFormat, clientName, clientIp, clientMachineName, clientOsName, clientVersion, creationTime, lastUpdateTime, terminationTime, firstScreenshotTime)
                .from(sessionRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.872+02:00", comments="Source Table: session")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(SessionRecord record) {
        return UpdateDSL.updateWithMapper(this::update, sessionRecord)
                .set(groupId).equalTo(record::getGroupId)
                .set(uuid).equalTo(record::getUuid)
                .set(imageFormat).equalTo(record::getImageFormat)
                .set(clientName).equalTo(record::getClientName)
                .set(clientIp).equalTo(record::getClientIp)
                .set(clientMachineName).equalTo(record::getClientMachineName)
                .set(clientOsName).equalTo(record::getClientOsName)
                .set(clientVersion).equalTo(record::getClientVersion)
                .set(creationTime).equalTo(record::getCreationTime)
                .set(lastUpdateTime).equalTo(record::getLastUpdateTime)
                .set(terminationTime).equalTo(record::getTerminationTime)
                .set(firstScreenshotTime).equalTo(record::getFirstScreenshotTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.872+02:00", comments="Source Table: session")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(SessionRecord record) {
        return UpdateDSL.updateWithMapper(this::update, sessionRecord)
                .set(groupId).equalToWhenPresent(record::getGroupId)
                .set(uuid).equalToWhenPresent(record::getUuid)
                .set(imageFormat).equalToWhenPresent(record::getImageFormat)
                .set(clientName).equalToWhenPresent(record::getClientName)
                .set(clientIp).equalToWhenPresent(record::getClientIp)
                .set(clientMachineName).equalToWhenPresent(record::getClientMachineName)
                .set(clientOsName).equalToWhenPresent(record::getClientOsName)
                .set(clientVersion).equalToWhenPresent(record::getClientVersion)
                .set(creationTime).equalToWhenPresent(record::getCreationTime)
                .set(lastUpdateTime).equalToWhenPresent(record::getLastUpdateTime)
                .set(terminationTime).equalToWhenPresent(record::getTerminationTime)
                .set(firstScreenshotTime).equalToWhenPresent(record::getFirstScreenshotTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.872+02:00", comments="Source Table: session")
    default int updateByPrimaryKey(SessionRecord record) {
        return UpdateDSL.updateWithMapper(this::update, sessionRecord)
                .set(groupId).equalTo(record::getGroupId)
                .set(uuid).equalTo(record::getUuid)
                .set(imageFormat).equalTo(record::getImageFormat)
                .set(clientName).equalTo(record::getClientName)
                .set(clientIp).equalTo(record::getClientIp)
                .set(clientMachineName).equalTo(record::getClientMachineName)
                .set(clientOsName).equalTo(record::getClientOsName)
                .set(clientVersion).equalTo(record::getClientVersion)
                .set(creationTime).equalTo(record::getCreationTime)
                .set(lastUpdateTime).equalTo(record::getLastUpdateTime)
                .set(terminationTime).equalTo(record::getTerminationTime)
                .set(firstScreenshotTime).equalTo(record::getFirstScreenshotTime)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-08-15T14:43:27.872+02:00", comments="Source Table: session")
    default int updateByPrimaryKeySelective(SessionRecord record) {
        return UpdateDSL.updateWithMapper(this::update, sessionRecord)
                .set(groupId).equalToWhenPresent(record::getGroupId)
                .set(uuid).equalToWhenPresent(record::getUuid)
                .set(imageFormat).equalToWhenPresent(record::getImageFormat)
                .set(clientName).equalToWhenPresent(record::getClientName)
                .set(clientIp).equalToWhenPresent(record::getClientIp)
                .set(clientMachineName).equalToWhenPresent(record::getClientMachineName)
                .set(clientOsName).equalToWhenPresent(record::getClientOsName)
                .set(clientVersion).equalToWhenPresent(record::getClientVersion)
                .set(creationTime).equalToWhenPresent(record::getCreationTime)
                .set(lastUpdateTime).equalToWhenPresent(record::getLastUpdateTime)
                .set(terminationTime).equalToWhenPresent(record::getTerminationTime)
                .set(firstScreenshotTime).equalToWhenPresent(record::getFirstScreenshotTime)
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
                        .from(sessionRecord);
    }
}