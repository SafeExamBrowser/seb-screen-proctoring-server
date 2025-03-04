package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.WebserviceInfoRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import ch.ethz.seb.sps.server.datalayer.batis.model.WebserviceInfoRecord;
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
public interface WebserviceInfoRecordMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<WebserviceInfoRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="server_address", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="master", javaType=Integer.class, jdbcType=JdbcType.INTEGER),
        @Arg(column="creation_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="last_update_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="termination_time", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    WebserviceInfoRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="server_address", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="master", javaType=Integer.class, jdbcType=JdbcType.INTEGER),
        @Arg(column="creation_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="last_update_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="termination_time", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    List<WebserviceInfoRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(webserviceInfoRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, webserviceInfoRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, webserviceInfoRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    default int insert(WebserviceInfoRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(webserviceInfoRecord)
                .map(uuid).toProperty("uuid")
                .map(serverAddress).toProperty("serverAddress")
                .map(master).toProperty("master")
                .map(creationTime).toProperty("creationTime")
                .map(lastUpdateTime).toProperty("lastUpdateTime")
                .map(terminationTime).toProperty("terminationTime")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    default int insertSelective(WebserviceInfoRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(webserviceInfoRecord)
                .map(uuid).toPropertyWhenPresent("uuid", record::getUuid)
                .map(serverAddress).toPropertyWhenPresent("serverAddress", record::getServerAddress)
                .map(master).toPropertyWhenPresent("master", record::getMaster)
                .map(creationTime).toPropertyWhenPresent("creationTime", record::getCreationTime)
                .map(lastUpdateTime).toPropertyWhenPresent("lastUpdateTime", record::getLastUpdateTime)
                .map(terminationTime).toPropertyWhenPresent("terminationTime", record::getTerminationTime)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<WebserviceInfoRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, uuid, serverAddress, master, creationTime, lastUpdateTime, terminationTime)
                .from(webserviceInfoRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<WebserviceInfoRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, uuid, serverAddress, master, creationTime, lastUpdateTime, terminationTime)
                .from(webserviceInfoRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    default WebserviceInfoRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, uuid, serverAddress, master, creationTime, lastUpdateTime, terminationTime)
                .from(webserviceInfoRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(WebserviceInfoRecord record) {
        return UpdateDSL.updateWithMapper(this::update, webserviceInfoRecord)
                .set(uuid).equalTo(record::getUuid)
                .set(serverAddress).equalTo(record::getServerAddress)
                .set(master).equalTo(record::getMaster)
                .set(creationTime).equalTo(record::getCreationTime)
                .set(lastUpdateTime).equalTo(record::getLastUpdateTime)
                .set(terminationTime).equalTo(record::getTerminationTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(WebserviceInfoRecord record) {
        return UpdateDSL.updateWithMapper(this::update, webserviceInfoRecord)
                .set(uuid).equalToWhenPresent(record::getUuid)
                .set(serverAddress).equalToWhenPresent(record::getServerAddress)
                .set(master).equalToWhenPresent(record::getMaster)
                .set(creationTime).equalToWhenPresent(record::getCreationTime)
                .set(lastUpdateTime).equalToWhenPresent(record::getLastUpdateTime)
                .set(terminationTime).equalToWhenPresent(record::getTerminationTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    default int updateByPrimaryKey(WebserviceInfoRecord record) {
        return UpdateDSL.updateWithMapper(this::update, webserviceInfoRecord)
                .set(uuid).equalTo(record::getUuid)
                .set(serverAddress).equalTo(record::getServerAddress)
                .set(master).equalTo(record::getMaster)
                .set(creationTime).equalTo(record::getCreationTime)
                .set(lastUpdateTime).equalTo(record::getLastUpdateTime)
                .set(terminationTime).equalTo(record::getTerminationTime)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-11-18T10:46:20.611+01:00", comments="Source Table: webservice_server_info")
    default int updateByPrimaryKeySelective(WebserviceInfoRecord record) {
        return UpdateDSL.updateWithMapper(this::update, webserviceInfoRecord)
                .set(uuid).equalToWhenPresent(record::getUuid)
                .set(serverAddress).equalToWhenPresent(record::getServerAddress)
                .set(master).equalToWhenPresent(record::getMaster)
                .set(creationTime).equalToWhenPresent(record::getCreationTime)
                .set(lastUpdateTime).equalToWhenPresent(record::getLastUpdateTime)
                .set(terminationTime).equalToWhenPresent(record::getTerminationTime)
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
                        .from(webserviceInfoRecord);
    }
}