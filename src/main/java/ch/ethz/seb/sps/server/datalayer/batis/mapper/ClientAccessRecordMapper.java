package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import ch.ethz.seb.sps.server.datalayer.batis.model.ClientAccessRecord;
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

import javax.annotation.Generated;
import java.util.List;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport.clientAccessRecord;
import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport.clientName;
import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport.clientSecret;
import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport.creationTime;
import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport.description;
import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport.id;
import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport.lastUpdateTime;
import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport.name;
import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport.owner;
import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport.terminationTime;
import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport.uuid;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Mapper
public interface ClientAccessRecordMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.323+02:00", comments="Source Table: client_access")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.323+02:00", comments="Source Table: client_access")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.323+02:00", comments="Source Table: client_access")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<ClientAccessRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.323+02:00", comments="Source Table: client_access")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="description", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="client_name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="client_secret", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="owner", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="creation_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="last_update_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="termination_time", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    ClientAccessRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.323+02:00", comments="Source Table: client_access")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="description", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="client_name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="client_secret", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="owner", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="creation_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="last_update_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="termination_time", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    List<ClientAccessRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.323+02:00", comments="Source Table: client_access")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.323+02:00", comments="Source Table: client_access")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(clientAccessRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.323+02:00", comments="Source Table: client_access")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, clientAccessRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.323+02:00", comments="Source Table: client_access")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, clientAccessRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.323+02:00", comments="Source Table: client_access")
    default int insert(ClientAccessRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(clientAccessRecord)
                .map(uuid).toProperty("uuid")
                .map(name).toProperty("name")
                .map(description).toProperty("description")
                .map(clientName).toProperty("clientName")
                .map(clientSecret).toProperty("clientSecret")
                .map(owner).toProperty("owner")
                .map(creationTime).toProperty("creationTime")
                .map(lastUpdateTime).toProperty("lastUpdateTime")
                .map(terminationTime).toProperty("terminationTime")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.323+02:00", comments="Source Table: client_access")
    default int insertSelective(ClientAccessRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(clientAccessRecord)
                .map(uuid).toPropertyWhenPresent("uuid", record::getUuid)
                .map(name).toPropertyWhenPresent("name", record::getName)
                .map(description).toPropertyWhenPresent("description", record::getDescription)
                .map(clientName).toPropertyWhenPresent("clientName", record::getClientName)
                .map(clientSecret).toPropertyWhenPresent("clientSecret", record::getClientSecret)
                .map(owner).toPropertyWhenPresent("owner", record::getOwner)
                .map(creationTime).toPropertyWhenPresent("creationTime", record::getCreationTime)
                .map(lastUpdateTime).toPropertyWhenPresent("lastUpdateTime", record::getLastUpdateTime)
                .map(terminationTime).toPropertyWhenPresent("terminationTime", record::getTerminationTime)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.324+02:00", comments="Source Table: client_access")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<ClientAccessRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, uuid, name, description, clientName, clientSecret, owner, creationTime, lastUpdateTime, terminationTime)
                .from(clientAccessRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.325+02:00", comments="Source Table: client_access")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<ClientAccessRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, uuid, name, description, clientName, clientSecret, owner, creationTime, lastUpdateTime, terminationTime)
                .from(clientAccessRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.325+02:00", comments="Source Table: client_access")
    default ClientAccessRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, uuid, name, description, clientName, clientSecret, owner, creationTime, lastUpdateTime, terminationTime)
                .from(clientAccessRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.325+02:00", comments="Source Table: client_access")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(ClientAccessRecord record) {
        return UpdateDSL.updateWithMapper(this::update, clientAccessRecord)
                .set(uuid).equalTo(record::getUuid)
                .set(name).equalTo(record::getName)
                .set(description).equalTo(record::getDescription)
                .set(clientName).equalTo(record::getClientName)
                .set(clientSecret).equalTo(record::getClientSecret)
                .set(owner).equalTo(record::getOwner)
                .set(creationTime).equalTo(record::getCreationTime)
                .set(lastUpdateTime).equalTo(record::getLastUpdateTime)
                .set(terminationTime).equalTo(record::getTerminationTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.325+02:00", comments="Source Table: client_access")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(ClientAccessRecord record) {
        return UpdateDSL.updateWithMapper(this::update, clientAccessRecord)
                .set(uuid).equalToWhenPresent(record::getUuid)
                .set(name).equalToWhenPresent(record::getName)
                .set(description).equalToWhenPresent(record::getDescription)
                .set(clientName).equalToWhenPresent(record::getClientName)
                .set(clientSecret).equalToWhenPresent(record::getClientSecret)
                .set(owner).equalToWhenPresent(record::getOwner)
                .set(creationTime).equalToWhenPresent(record::getCreationTime)
                .set(lastUpdateTime).equalToWhenPresent(record::getLastUpdateTime)
                .set(terminationTime).equalToWhenPresent(record::getTerminationTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.325+02:00", comments="Source Table: client_access")
    default int updateByPrimaryKey(ClientAccessRecord record) {
        return UpdateDSL.updateWithMapper(this::update, clientAccessRecord)
                .set(uuid).equalTo(record::getUuid)
                .set(name).equalTo(record::getName)
                .set(description).equalTo(record::getDescription)
                .set(clientName).equalTo(record::getClientName)
                .set(clientSecret).equalTo(record::getClientSecret)
                .set(owner).equalTo(record::getOwner)
                .set(creationTime).equalTo(record::getCreationTime)
                .set(lastUpdateTime).equalTo(record::getLastUpdateTime)
                .set(terminationTime).equalTo(record::getTerminationTime)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-13T17:30:00.325+02:00", comments="Source Table: client_access")
    default int updateByPrimaryKeySelective(ClientAccessRecord record) {
        return UpdateDSL.updateWithMapper(this::update, clientAccessRecord)
                .set(uuid).equalToWhenPresent(record::getUuid)
                .set(name).equalToWhenPresent(record::getName)
                .set(description).equalToWhenPresent(record::getDescription)
                .set(clientName).equalToWhenPresent(record::getClientName)
                .set(clientSecret).equalToWhenPresent(record::getClientSecret)
                .set(owner).equalToWhenPresent(record::getOwner)
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
                        .from(clientAccessRecord);
    }
}