package ch.ethz.seb.sps.server.datalayer.batis.custommappers;

import ch.ethz.seb.sps.domain.model.PageSortOrder;
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

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordDynamicSqlSupport.screenshotDataRecord;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThanWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThanWhenPresent;

import java.sql.Date;
import java.util.List;

@Mapper
public interface ScreenshotDataMapper {

    @SelectProvider(type= SqlProviderAdapter.class, method="select")
    @ConstructorArgs({@Arg(column="timestamp", javaType=Long.class, jdbcType= JdbcType.BIGINT, id=true)})
    List<Long> selectTimestamps(SelectStatementProvider select);

    @SelectProvider(type = CustomSqlProvider.class, method = "selectDistinctTimestamps")
    List<Date> selectDistinctTimestamps(SelectStatementProvider selectStatement);


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

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<Date>>> selectTimestampsAsDates(){
        return SelectDSL.selectDistinctWithMapper(this::selectDistinctTimestamps, ScreenshotDataRecordDynamicSqlSupport.timestamp)
                .from(screenshotDataRecord);
    }


    class CustomSqlProvider {
        public String selectDistinctTimestamps(SelectStatementProvider selectStatement) {
            String query = selectStatement.getSelectStatement();
            int indexWhere = query.toLowerCase().indexOf("from");

            String fromClause = query.substring(indexWhere);
            query = "SELECT DISTINCT " +
                    "DATE(FROM_UNIXTIME(" + ScreenshotDataRecordDynamicSqlSupport.timestamp.name() + "/ 1000)) as timestamp "
                    + fromClause;

            return query;
        }
    }

}