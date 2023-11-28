package ch.ethz.seb.sps.server.datalayer.batis.custommappers;

import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordDynamicSqlSupport;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.select.MyBatis3SelectModelAdapter;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.List;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordDynamicSqlSupport.screenshotDataRecord;

@Mapper
public interface ScreenshotDataMapper {

    @SelectProvider(type= SqlProviderAdapter.class, method="select")
    @ConstructorArgs({@Arg(column="timestamp", javaType=Long.class, jdbcType= JdbcType.BIGINT, id=true)})
    List<Long> selectTimestamps(SelectStatementProvider select);

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<Long>>>.QueryExpressionWhereBuilder selectScreenshotTimestamps(final String sessionUUID) {
        return SelectDSL.selectWithMapper(this::selectTimestamps, ScreenshotDataRecordDynamicSqlSupport.timestamp)
                .from(screenshotDataRecord)
                .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, isEqualTo(sessionUUID));
    }
}
