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
import org.apache.ibatis.annotations.SelectKey;
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
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.595+02:00", comments="Source Table: SESSION")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.595+02:00", comments="Source Table: SESSION")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.595+02:00", comments="Source Table: SESSION")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="record.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<SessionRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.595+02:00", comments="Source Table: SESSION")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="ID", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="GROUP_ID", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="UUID", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="NAME", javaType=String.class, jdbcType=JdbcType.VARCHAR)
    })
    SessionRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.595+02:00", comments="Source Table: SESSION")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="ID", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="GROUP_ID", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="UUID", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="NAME", javaType=String.class, jdbcType=JdbcType.VARCHAR)
    })
    List<SessionRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.595+02:00", comments="Source Table: SESSION")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.595+02:00", comments="Source Table: SESSION")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(sessionRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.596+02:00", comments="Source Table: SESSION")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, sessionRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.596+02:00", comments="Source Table: SESSION")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, sessionRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.596+02:00", comments="Source Table: SESSION")
    default int insert(SessionRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(sessionRecord)
                .map(groupId).toProperty("groupId")
                .map(uuid).toProperty("uuid")
                .map(name).toProperty("name")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.596+02:00", comments="Source Table: SESSION")
    default int insertSelective(SessionRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(sessionRecord)
                .map(groupId).toPropertyWhenPresent("groupId", record::getGroupId)
                .map(uuid).toPropertyWhenPresent("uuid", record::getUuid)
                .map(name).toPropertyWhenPresent("name", record::getName)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.596+02:00", comments="Source Table: SESSION")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<SessionRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, groupId, uuid, name)
                .from(sessionRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.596+02:00", comments="Source Table: SESSION")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<SessionRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, groupId, uuid, name)
                .from(sessionRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.596+02:00", comments="Source Table: SESSION")
    default SessionRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, groupId, uuid, name)
                .from(sessionRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.596+02:00", comments="Source Table: SESSION")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(SessionRecord record) {
        return UpdateDSL.updateWithMapper(this::update, sessionRecord)
                .set(groupId).equalTo(record::getGroupId)
                .set(uuid).equalTo(record::getUuid)
                .set(name).equalTo(record::getName);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.596+02:00", comments="Source Table: SESSION")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(SessionRecord record) {
        return UpdateDSL.updateWithMapper(this::update, sessionRecord)
                .set(groupId).equalToWhenPresent(record::getGroupId)
                .set(uuid).equalToWhenPresent(record::getUuid)
                .set(name).equalToWhenPresent(record::getName);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.596+02:00", comments="Source Table: SESSION")
    default int updateByPrimaryKey(SessionRecord record) {
        return UpdateDSL.updateWithMapper(this::update, sessionRecord)
                .set(groupId).equalTo(record::getGroupId)
                .set(uuid).equalTo(record::getUuid)
                .set(name).equalTo(record::getName)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.597+02:00", comments="Source Table: SESSION")
    default int updateByPrimaryKeySelective(SessionRecord record) {
        return UpdateDSL.updateWithMapper(this::update, sessionRecord)
                .set(groupId).equalToWhenPresent(record::getGroupId)
                .set(uuid).equalToWhenPresent(record::getUuid)
                .set(name).equalToWhenPresent(record::getName)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator",comments="Source Table: exam")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({@Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true)})
    List<Long> selectIds(SelectStatementProvider select);

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<Long>>> selectIdsByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectIds, id)
                        .from(sessionRecord);
    }
}