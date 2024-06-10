package ch.ethz.seb.sps.server.datalayer.batis.custommappers;

import ch.ethz.seb.sps.server.datalayer.batis.mapper.SessionRecordDynamicSqlSupport;
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

import java.sql.Date;
import java.util.List;

@Mapper
public interface SessionMapper {

    class SqlProvider {
        public String selectDistinctCreationDates(SelectStatementProvider selectStatement) {
            String query = selectStatement.getSelectStatement();
            int indexWhere = query.toLowerCase().indexOf("from");

            String fromClause = query.substring(indexWhere);
            query = "SELECT DISTINCT DATE(FROM_UNIXTIME(creation_time / 1000)) as creation_time " + fromClause;

            return query;
        }
    }

    @SelectProvider(type = SqlProvider.class, method = "selectDistinctCreationDates")
    List<Date> selectMany(SelectStatementProvider selectStatement);

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<Date>>> getCreationTimes(){
        return SelectDSL.selectDistinctWithMapper(this::selectMany, SessionRecordDynamicSqlSupport.creationTime)
                .from(SessionRecordDynamicSqlSupport.sessionRecord);
    }

}