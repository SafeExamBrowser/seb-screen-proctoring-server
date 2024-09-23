package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.UserRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import ch.ethz.seb.sps.server.datalayer.batis.model.UserRecord;
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
public interface UserRecordMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.259+02:00", comments="Source Table: user")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.261+02:00", comments="Source Table: user")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.261+02:00", comments="Source Table: user")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<UserRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.264+02:00", comments="Source Table: user")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="surname", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="username", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="password", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="email", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="language", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="timezone", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="roles", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="creation_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="last_update_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="termination_time", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    UserRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.265+02:00", comments="Source Table: user")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="surname", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="username", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="password", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="email", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="language", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="timezone", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="roles", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="creation_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="last_update_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="termination_time", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    List<UserRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.266+02:00", comments="Source Table: user")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.266+02:00", comments="Source Table: user")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(userRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.267+02:00", comments="Source Table: user")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, userRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.268+02:00", comments="Source Table: user")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, userRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.269+02:00", comments="Source Table: user")
    default int insert(UserRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(userRecord)
                .map(uuid).toProperty("uuid")
                .map(name).toProperty("name")
                .map(surname).toProperty("surname")
                .map(username).toProperty("username")
                .map(password).toProperty("password")
                .map(email).toProperty("email")
                .map(language).toProperty("language")
                .map(timezone).toProperty("timezone")
                .map(roles).toProperty("roles")
                .map(creationTime).toProperty("creationTime")
                .map(lastUpdateTime).toProperty("lastUpdateTime")
                .map(terminationTime).toProperty("terminationTime")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.270+02:00", comments="Source Table: user")
    default int insertSelective(UserRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(userRecord)
                .map(uuid).toPropertyWhenPresent("uuid", record::getUuid)
                .map(name).toPropertyWhenPresent("name", record::getName)
                .map(surname).toPropertyWhenPresent("surname", record::getSurname)
                .map(username).toPropertyWhenPresent("username", record::getUsername)
                .map(password).toPropertyWhenPresent("password", record::getPassword)
                .map(email).toPropertyWhenPresent("email", record::getEmail)
                .map(language).toPropertyWhenPresent("language", record::getLanguage)
                .map(timezone).toPropertyWhenPresent("timezone", record::getTimezone)
                .map(roles).toPropertyWhenPresent("roles", record::getRoles)
                .map(creationTime).toPropertyWhenPresent("creationTime", record::getCreationTime)
                .map(lastUpdateTime).toPropertyWhenPresent("lastUpdateTime", record::getLastUpdateTime)
                .map(terminationTime).toPropertyWhenPresent("terminationTime", record::getTerminationTime)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.271+02:00", comments="Source Table: user")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<UserRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, uuid, name, surname, username, password, email, language, timezone, roles, creationTime, lastUpdateTime, terminationTime)
                .from(userRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.273+02:00", comments="Source Table: user")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<UserRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, uuid, name, surname, username, password, email, language, timezone, roles, creationTime, lastUpdateTime, terminationTime)
                .from(userRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.274+02:00", comments="Source Table: user")
    default UserRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, uuid, name, surname, username, password, email, language, timezone, roles, creationTime, lastUpdateTime, terminationTime)
                .from(userRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.275+02:00", comments="Source Table: user")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(UserRecord record) {
        return UpdateDSL.updateWithMapper(this::update, userRecord)
                .set(uuid).equalTo(record::getUuid)
                .set(name).equalTo(record::getName)
                .set(surname).equalTo(record::getSurname)
                .set(username).equalTo(record::getUsername)
                .set(password).equalTo(record::getPassword)
                .set(email).equalTo(record::getEmail)
                .set(language).equalTo(record::getLanguage)
                .set(timezone).equalTo(record::getTimezone)
                .set(roles).equalTo(record::getRoles)
                .set(creationTime).equalTo(record::getCreationTime)
                .set(lastUpdateTime).equalTo(record::getLastUpdateTime)
                .set(terminationTime).equalTo(record::getTerminationTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.277+02:00", comments="Source Table: user")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(UserRecord record) {
        return UpdateDSL.updateWithMapper(this::update, userRecord)
                .set(uuid).equalToWhenPresent(record::getUuid)
                .set(name).equalToWhenPresent(record::getName)
                .set(surname).equalToWhenPresent(record::getSurname)
                .set(username).equalToWhenPresent(record::getUsername)
                .set(password).equalToWhenPresent(record::getPassword)
                .set(email).equalToWhenPresent(record::getEmail)
                .set(language).equalToWhenPresent(record::getLanguage)
                .set(timezone).equalToWhenPresent(record::getTimezone)
                .set(roles).equalToWhenPresent(record::getRoles)
                .set(creationTime).equalToWhenPresent(record::getCreationTime)
                .set(lastUpdateTime).equalToWhenPresent(record::getLastUpdateTime)
                .set(terminationTime).equalToWhenPresent(record::getTerminationTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.278+02:00", comments="Source Table: user")
    default int updateByPrimaryKey(UserRecord record) {
        return UpdateDSL.updateWithMapper(this::update, userRecord)
                .set(uuid).equalTo(record::getUuid)
                .set(name).equalTo(record::getName)
                .set(surname).equalTo(record::getSurname)
                .set(username).equalTo(record::getUsername)
                .set(password).equalTo(record::getPassword)
                .set(email).equalTo(record::getEmail)
                .set(language).equalTo(record::getLanguage)
                .set(timezone).equalTo(record::getTimezone)
                .set(roles).equalTo(record::getRoles)
                .set(creationTime).equalTo(record::getCreationTime)
                .set(lastUpdateTime).equalTo(record::getLastUpdateTime)
                .set(terminationTime).equalTo(record::getTerminationTime)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2024-09-03T11:11:55.279+02:00", comments="Source Table: user")
    default int updateByPrimaryKeySelective(UserRecord record) {
        return UpdateDSL.updateWithMapper(this::update, userRecord)
                .set(uuid).equalToWhenPresent(record::getUuid)
                .set(name).equalToWhenPresent(record::getName)
                .set(surname).equalToWhenPresent(record::getSurname)
                .set(username).equalToWhenPresent(record::getUsername)
                .set(password).equalToWhenPresent(record::getPassword)
                .set(email).equalToWhenPresent(record::getEmail)
                .set(language).equalToWhenPresent(record::getLanguage)
                .set(timezone).equalToWhenPresent(record::getTimezone)
                .set(roles).equalToWhenPresent(record::getRoles)
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
                        .from(userRecord);
    }
}