package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import ch.ethz.seb.sps.server.datalayer.batis.JodaTimeTypeResolver;
import ch.ethz.seb.sps.server.datalayer.batis.model.ClientAccessRecord;
import java.util.List;
import javax.annotation.Generated;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import org.joda.time.DateTime;
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
public interface ClientAccessRecordMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.500+02:00", comments="Source Table: client_access")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.500+02:00", comments="Source Table: client_access")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.500+02:00", comments="Source Table: client_access")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="record.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<ClientAccessRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.500+02:00", comments="Source Table: client_access")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="client_name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="client_secret", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="creation_date", javaType=DateTime.class, typeHandler=JodaTimeTypeResolver.class, jdbcType=JdbcType.TIMESTAMP),
        @Arg(column="active", javaType=Integer.class, jdbcType=JdbcType.INTEGER)
    })
    ClientAccessRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.500+02:00", comments="Source Table: client_access")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="client_name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="client_secret", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="creation_date", javaType=DateTime.class, typeHandler=JodaTimeTypeResolver.class, jdbcType=JdbcType.TIMESTAMP),
        @Arg(column="active", javaType=Integer.class, jdbcType=JdbcType.INTEGER)
    })
    List<ClientAccessRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.500+02:00", comments="Source Table: client_access")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.501+02:00", comments="Source Table: client_access")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(clientAccessRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.501+02:00", comments="Source Table: client_access")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, clientAccessRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.501+02:00", comments="Source Table: client_access")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, clientAccessRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.501+02:00", comments="Source Table: client_access")
    default int insert(ClientAccessRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(clientAccessRecord)
                .map(clientName).toProperty("clientName")
                .map(clientSecret).toProperty("clientSecret")
                .map(creationDate).toProperty("creationDate")
                .map(active).toProperty("active")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.501+02:00", comments="Source Table: client_access")
    default int insertSelective(ClientAccessRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(clientAccessRecord)
                .map(clientName).toPropertyWhenPresent("clientName", record::getClientName)
                .map(clientSecret).toPropertyWhenPresent("clientSecret", record::getClientSecret)
                .map(creationDate).toPropertyWhenPresent("creationDate", record::getCreationDate)
                .map(active).toPropertyWhenPresent("active", record::getActive)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.501+02:00", comments="Source Table: client_access")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<ClientAccessRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, clientName, clientSecret, creationDate, active)
                .from(clientAccessRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.501+02:00", comments="Source Table: client_access")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<ClientAccessRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, clientName, clientSecret, creationDate, active)
                .from(clientAccessRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.502+02:00", comments="Source Table: client_access")
    default ClientAccessRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, clientName, clientSecret, creationDate, active)
                .from(clientAccessRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.502+02:00", comments="Source Table: client_access")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(ClientAccessRecord record) {
        return UpdateDSL.updateWithMapper(this::update, clientAccessRecord)
                .set(clientName).equalTo(record::getClientName)
                .set(clientSecret).equalTo(record::getClientSecret)
                .set(creationDate).equalTo(record::getCreationDate)
                .set(active).equalTo(record::getActive);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.502+02:00", comments="Source Table: client_access")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(ClientAccessRecord record) {
        return UpdateDSL.updateWithMapper(this::update, clientAccessRecord)
                .set(clientName).equalToWhenPresent(record::getClientName)
                .set(clientSecret).equalToWhenPresent(record::getClientSecret)
                .set(creationDate).equalToWhenPresent(record::getCreationDate)
                .set(active).equalToWhenPresent(record::getActive);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.502+02:00", comments="Source Table: client_access")
    default int updateByPrimaryKey(ClientAccessRecord record) {
        return UpdateDSL.updateWithMapper(this::update, clientAccessRecord)
                .set(clientName).equalTo(record::getClientName)
                .set(clientSecret).equalTo(record::getClientSecret)
                .set(creationDate).equalTo(record::getCreationDate)
                .set(active).equalTo(record::getActive)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.502+02:00", comments="Source Table: client_access")
    default int updateByPrimaryKeySelective(ClientAccessRecord record) {
        return UpdateDSL.updateWithMapper(this::update, clientAccessRecord)
                .set(clientName).equalToWhenPresent(record::getClientName)
                .set(clientSecret).equalToWhenPresent(record::getClientSecret)
                .set(creationDate).equalToWhenPresent(record::getCreationDate)
                .set(active).equalToWhenPresent(record::getActive)
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