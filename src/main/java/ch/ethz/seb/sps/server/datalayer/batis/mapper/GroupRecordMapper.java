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
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.591+02:00", comments="Source Table: GROUP")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.591+02:00", comments="Source Table: GROUP")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.591+02:00", comments="Source Table: GROUP")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="record.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<GroupRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.591+02:00", comments="Source Table: GROUP")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="ID", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="UUID", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="NAME", javaType=String.class, jdbcType=JdbcType.VARCHAR)
    })
    GroupRecord selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.591+02:00", comments="Source Table: GROUP")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({
        @Arg(column="ID", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true),
        @Arg(column="UUID", javaType=String.class, jdbcType=JdbcType.VARCHAR),
        @Arg(column="NAME", javaType=String.class, jdbcType=JdbcType.VARCHAR)
    })
    List<GroupRecord> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.592+02:00", comments="Source Table: GROUP")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.592+02:00", comments="Source Table: GROUP")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(groupRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.592+02:00", comments="Source Table: GROUP")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, groupRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.592+02:00", comments="Source Table: GROUP")
    default int deleteByPrimaryKey(Long id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, groupRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.592+02:00", comments="Source Table: GROUP")
    default int insert(GroupRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(groupRecord)
                .map(uuid).toProperty("uuid")
                .map(name).toProperty("name")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.592+02:00", comments="Source Table: GROUP")
    default int insertSelective(GroupRecord record) {
        return insert(SqlBuilder.insert(record)
                .into(groupRecord)
                .map(uuid).toPropertyWhenPresent("uuid", record::getUuid)
                .map(name).toPropertyWhenPresent("name", record::getName)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.592+02:00", comments="Source Table: GROUP")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<GroupRecord>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, uuid, name)
                .from(groupRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.592+02:00", comments="Source Table: GROUP")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<GroupRecord>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, uuid, name)
                .from(groupRecord);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.592+02:00", comments="Source Table: GROUP")
    default GroupRecord selectByPrimaryKey(Long id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, uuid, name)
                .from(groupRecord)
                .where(id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.592+02:00", comments="Source Table: GROUP")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(GroupRecord record) {
        return UpdateDSL.updateWithMapper(this::update, groupRecord)
                .set(uuid).equalTo(record::getUuid)
                .set(name).equalTo(record::getName);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.593+02:00", comments="Source Table: GROUP")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(GroupRecord record) {
        return UpdateDSL.updateWithMapper(this::update, groupRecord)
                .set(uuid).equalToWhenPresent(record::getUuid)
                .set(name).equalToWhenPresent(record::getName);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.593+02:00", comments="Source Table: GROUP")
    default int updateByPrimaryKey(GroupRecord record) {
        return UpdateDSL.updateWithMapper(this::update, groupRecord)
                .set(uuid).equalTo(record::getUuid)
                .set(name).equalTo(record::getName)
                .where(id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-09-23T10:10:02.593+02:00", comments="Source Table: GROUP")
    default int updateByPrimaryKeySelective(GroupRecord record) {
        return UpdateDSL.updateWithMapper(this::update, groupRecord)
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
                        .from(groupRecord);
    }
}