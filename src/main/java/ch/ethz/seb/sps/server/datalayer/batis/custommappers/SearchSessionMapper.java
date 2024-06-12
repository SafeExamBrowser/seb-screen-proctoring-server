package ch.ethz.seb.sps.server.datalayer.batis.custommappers;

import ch.ethz.seb.sps.server.datalayer.batis.mapper.SessionRecordDynamicSqlSupport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.mybatis.dynamic.sql.select.MyBatis3SelectModelAdapter;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;

import java.sql.Date;
import java.util.List;

@Mapper
public interface SearchSessionMapper {

    @SelectProvider(type = CustomSqlProvider.class, method = "selectDistinctCreationDates")
    List<Date> selectDistinctCreationTimes(SelectStatementProvider selectStatement);

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<Date>>> selectCreationTimesAsDates(){
        return SelectDSL.selectDistinctWithMapper(this::selectDistinctCreationTimes, SessionRecordDynamicSqlSupport.creationTime)
                .from(SessionRecordDynamicSqlSupport.sessionRecord);
    }

    class CustomSqlProvider {
        public String selectDistinctCreationDates(SelectStatementProvider selectStatement) {
            String query = selectStatement.getSelectStatement();
            int indexWhere = query.toLowerCase().indexOf("from");

            String fromClause = query.substring(indexWhere);
            query = "SELECT DISTINCT " +
                    "DATE(FROM_UNIXTIME(" + SessionRecordDynamicSqlSupport.creationTime.name() + "/ 1000)) as creation_time "
                    + fromClause;

            return query;
        }
    }

}