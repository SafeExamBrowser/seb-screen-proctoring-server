package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import ch.ethz.seb.sps.server.datalayer.batis.model.GroupRecord;
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
public interface GroupRecordMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.507+02:00", comments="Source Table: seb_group")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.507+02:00", comments="Source Table: seb_group")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.508+02:00", comments="Source Table: seb_group")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="record.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<GroupRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.508+02:00", comments="Source Table: seb_group")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="creation_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="termination_time", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    GroupRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.508+02:00", comments="Source Table: seb_group")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="id", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="uuid", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="name", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="creation_time", javaType=Long.class, jdbcType=JdbcType.BIGINT),
        @Arg(column="termination_time", javaType=Long.class, jdbcType=JdbcType.BIGINT)
    })
    List<GroupRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.508+02:00", comments="Source Table: seb_group")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.508+02:00", comments="Source Table: seb_group")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(groupRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.509+02:00", comments="Source Table: seb_group")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, groupRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.509+02:00", comments="Source Table: seb_group")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, groupRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.509+02:00", comments="Source Table: seb_group")
    default int insert(GroupRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(groupRecord)
                .map(uuid).toProperty("uuid")
                .map(name).toProperty("name")
                .map(creationTime).toProperty("creationTime")
                .map(terminationTime).toProperty("terminationTime")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.509+02:00", comments="Source Table: seb_group")
    default int insertSelective(GroupRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(groupRecord)
                .map(uuid).toPropertyWhenPresent("uuid", record::getUuid)
                .map(name).toPropertyWhenPresent("name", record::getName)
                .map(creationTime).toPropertyWhenPresent("creationTime", record::getCreationTime)
                .map(terminationTime).toPropertyWhenPresent("terminationTime", record::getTerminationTime)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.509+02:00", comments="Source Table: seb_group")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<GroupRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, uuid, name, creationTime, terminationTime)
                .from(groupRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.509+02:00", comments="Source Table: seb_group")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<GroupRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, uuid, name, creationTime, terminationTime)
                .from(groupRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.509+02:00", comments="Source Table: seb_group")
    default GroupRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, uuid, name, creationTime, terminationTime)
                .from(groupRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.509+02:00", comments="Source Table: seb_group")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(GroupRecord record) {
        return UpdateDSL.updateWithMapper(this::update, groupRecord)
                .set(uuid).equalTo(record::getUuid)
                .set(name).equalTo(record::getName)
                .set(creationTime).equalTo(record::getCreationTime)
                .set(terminationTime).equalTo(record::getTerminationTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.510+02:00", comments="Source Table: seb_group")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(GroupRecord record) {
        return UpdateDSL.updateWithMapper(this::update, groupRecord)
                .set(uuid).equalToWhenPresent(record::getUuid)
                .set(name).equalToWhenPresent(record::getName)
                .set(creationTime).equalToWhenPresent(record::getCreationTime)
                .set(terminationTime).equalToWhenPresent(record::getTerminationTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.510+02:00", comments="Source Table: seb_group")
    default int updateByPrimaryKey(GroupRecord record) {
        return UpdateDSL.updateWithMapper(this::update, groupRecord)
                .set(uuid).equalTo(record::getUuid)
                .set(name).equalTo(record::getName)
                .set(creationTime).equalTo(record::getCreationTime)
                .set(terminationTime).equalTo(record::getTerminationTime)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-27T17:51:00.510+02:00", comments="Source Table: seb_group")
    default int updateByPrimaryKeySelective(GroupRecord record) {
        return UpdateDSL.updateWithMapper(this::update, groupRecord)
                .set(uuid).equalToWhenPresent(record::getUuid)
                .set(name).equalToWhenPresent(record::getName)
                .set(creationTime).equalToWhenPresent(record::getCreationTime)
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
                        .from(groupRecord);
    }
}