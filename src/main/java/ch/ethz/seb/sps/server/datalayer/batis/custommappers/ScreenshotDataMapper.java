package ch.ethz.seb.sps.server.datalayer.batis.custommappers;

import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordDynamicSqlSupport;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.select.MyBatis3SelectModelAdapter;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordDynamicSqlSupport.screenshotDataRecord;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThanWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThanWhenPresent;

@Mapper
public interface ScreenshotDataMapper {

    @SelectProvider(type= SqlProviderAdapter.class, method="select")
    @ConstructorArgs({@Arg(column="timestamp", javaType=Long.class, jdbcType= JdbcType.BIGINT, id=true)})
    List<Long> selectTimestamps(SelectStatementProvider select);

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<Long>>>.QueryExpressionWhereBuilder selectScreenshotTimestamps(
            final String sessionUUID,
            final Long timestamp,
            final PageSortOrder sortOrder ) {

        if(sortOrder.equals(PageSortOrder.DESCENDING)){
            return SelectDSL.selectWithMapper(this::selectTimestamps, ScreenshotDataRecordDynamicSqlSupport.timestamp)
                    .from(screenshotDataRecord)
                    .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, isEqualTo(sessionUUID))
                    .and(ScreenshotDataRecordDynamicSqlSupport.timestamp, isLessThanWhenPresent(timestamp));
        }

        return SelectDSL.selectWithMapper(this::selectTimestamps, ScreenshotDataRecordDynamicSqlSupport.timestamp)
                .from(screenshotDataRecord)
                .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, isEqualTo(sessionUUID))
                .and(ScreenshotDataRecordDynamicSqlSupport.timestamp, isGreaterThanWhenPresent(timestamp));
    }

    class SqlProvider {
        public String countMatchingScreenshotDataPerDay(SelectStatementProvider selectStatement, @Param("parameters") Map<String, Object> parameters, Date date) {
            String baseQuery = selectStatement.getSelectStatement();

            String selectAndCount = "SELECT COUNT(session_uuid) ";
            String fromClause = baseQuery.substring(baseQuery.indexOf("from"));

            String whereTimestampStart = " AND timestamp >= UNIX_TIMESTAMP('" + date + "') * 1000";
            String whereTimestampEnd = " AND timestamp <= (UNIX_TIMESTAMP('" + date + "') + 86400) * 1000";

            String fullQuery = selectAndCount + fromClause + whereTimestampStart + whereTimestampEnd;

            return fullQuery;
        }
    }

    @SelectProvider(type = SqlProvider.class, method = "countMatchingScreenshotDataPerDay")
    Long countScreenshotDataPerDay(SelectStatementProvider selectStatement, @Param("parameters") Map<String, Object> parameters, Date date);

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countMatchingScreenshotDataPerDay(final Date date) {

        return SelectDSL.selectWithMapper((selectStatement) -> countScreenshotDataPerDay(selectStatement, selectStatement.getParameters(), date))
                .from(screenshotDataRecord);
    }

}