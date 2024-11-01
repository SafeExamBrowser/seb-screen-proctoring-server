package ch.ethz.seb.sps.server.datalayer.batis.custommappers;

import ch.ethz.seb.sps.domain.model.service.UserListForApplicationSearch;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport;
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

import java.util.List;


@Mapper
public interface SearchApplicationMapper {

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({@Arg(column="id", javaType=String.class, jdbcType=JdbcType.VARCHAR, id=true)})
    List<Long> selectDistinctGroupIds(SelectStatementProvider select);

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<Long>>> selectDistinctGroupIds() {
        return SelectDSL.selectDistinctWithMapper(this::selectDistinctGroupIds, GroupRecordDynamicSqlSupport.id)
                .from(GroupRecordDynamicSqlSupport.groupRecord);
    }

    //----------------------------------------------------------------------------------------------------------------------------

    @SelectProvider(type=CustomSQLProvider.class, method="selectDistinctMetadataAppForExam")
    List<String> selectDistinctMetadataAppForExam(SelectStatementProvider select);

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<String>>> selectDistinctMetadataAppForExam() {
        return SelectDSL.selectWithMapper(this::selectDistinctMetadataAppForExam)
                .from(ScreenshotDataRecordDynamicSqlSupport.screenshotDataRecord);
    }

    //----------------------------------------------------------------------------------------------------------------------------

    @SelectProvider(type=CustomSQLProvider.class, method="selectDistinctWindowTitle")
    List<String> selectDistinctWindowTitle(SelectStatementProvider select);

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<String>>> selectDistinctWindowTitle() {
        return SelectDSL.selectWithMapper(this::selectDistinctWindowTitle)
                .from(ScreenshotDataRecordDynamicSqlSupport.screenshotDataRecord);
    }

    //----------------------------------------------------------------------------------------------------------------------------

    @SelectProvider(type=CustomSQLProvider.class, method="selectUserListForApplicationSearch")
    @ConstructorArgs({
            @Arg(column="client_name", javaType=String.class, jdbcType= JdbcType.VARCHAR),
            @Arg(column="uuid", javaType=String.class, jdbcType= JdbcType.VARCHAR),
            @Arg(column="timestamp", javaType= Long.class, jdbcType= JdbcType.BIGINT),
            @Arg(column="count", javaType=Integer.class, jdbcType= JdbcType.INTEGER),
    })
    List<UserListForApplicationSearch> selectUserListForApplicationSearch(SelectStatementProvider select);

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<UserListForApplicationSearch>>> selectUserListForApplicationSearch() {
        return SelectDSL.selectWithMapper(this::selectUserListForApplicationSearch)
                .from(ScreenshotDataRecordDynamicSqlSupport.screenshotDataRecord);
    }

    //----------------------------------------------------------------------------------------------------------------------------

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ConstructorArgs({@Arg(column="timestamp", javaType=Long.class, jdbcType=JdbcType.BIGINT)})
    List<Long> selectTimestampListForApplicationSearch(SelectStatementProvider select);

    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<Long>>> selectTimestampListForApplicationSearch() {
        return SelectDSL.selectWithMapper(this::selectTimestampListForApplicationSearch, ScreenshotDataRecordDynamicSqlSupport.timestamp)
                .from(ScreenshotDataRecordDynamicSqlSupport.screenshotDataRecord);
    }

    //----------------------------------------------------------------------------------------------------------------------------

    class CustomSQLProvider{
        public String selectDistinctMetadataAppForExam(SelectStatementProvider selectStatement) {
            String query = selectStatement.getSelectStatement();
            String fromClause = getFromClause(query);

            query = "SELECT DISTINCT " +
                    "JSON_UNQUOTE(JSON_EXTRACT(`screenshot_data`.meta_data, '$.screenProctoringMetadataApplication')) AS screenProctoringMetadataApplication " +
                    fromClause +
                    " ORDER BY screenProctoringMetadataApplication";

            return query;
        }

        public String selectDistinctWindowTitle(SelectStatementProvider selectStatement) {
            String query = selectStatement.getSelectStatement();
            String fromClause = getFromClause(query);

            query = "SELECT DISTINCT " +
                    "JSON_UNQUOTE(JSON_EXTRACT(`screenshot_data`.meta_data, '$.screenProctoringMetadataWindowTitle')) AS screenProctoringMetadataWindowTitle " +
                    fromClause +
                    " ORDER BY screenProctoringMetadataWindowTitle";

            return query;
        }

        public String selectUserListForApplicationSearch(SelectStatementProvider selectStatement){
            String query = selectStatement.getSelectStatement();
            String fromClause = getFromClause(query);

            query = "SELECT " +
                    "`session`.client_name, " +
                    "MIN(`session`.uuid) AS uuid, " +
                    "MIN(`screenshot_data`.timestamp) AS timestamp, " +
                    "COUNT(*) as count " +
                    fromClause +
                    " GROUP BY `session`.client_name" +
                    " ORDER BY `session`.client_name";

            return query;
        }

        private String getFromClause(final String query){
            int indexWhere = query.toLowerCase().indexOf("from");
            return query.substring(indexWhere);
        }
    }

}