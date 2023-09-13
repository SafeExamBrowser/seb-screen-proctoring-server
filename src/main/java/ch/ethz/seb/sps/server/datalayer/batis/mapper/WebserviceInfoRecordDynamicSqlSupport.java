package ch.ethz.seb.sps.server.datalayer.batis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class WebserviceInfoRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.600+02:00", comments="Source Table: webservice_server_info")
    public static final WebserviceInfoRecord webserviceInfoRecord = new WebserviceInfoRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: webservice_server_info.id")
    public static final SqlColumn<Long> id = webserviceInfoRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: webservice_server_info.uuid")
    public static final SqlColumn<String> uuid = webserviceInfoRecord.uuid;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: webservice_server_info.server_address")
    public static final SqlColumn<String> serverAddress = webserviceInfoRecord.serverAddress;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: webservice_server_info.master")
    public static final SqlColumn<Integer> master = webserviceInfoRecord.master;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: webservice_server_info.creation_time")
    public static final SqlColumn<Long> creationTime = webserviceInfoRecord.creationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: webservice_server_info.last_update_time")
    public static final SqlColumn<Long> lastUpdateTime = webserviceInfoRecord.lastUpdateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source field: webservice_server_info.termination_time")
    public static final SqlColumn<Long> terminationTime = webserviceInfoRecord.terminationTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-09-07T09:53:44.601+02:00", comments="Source Table: webservice_server_info")
    public static final class WebserviceInfoRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> uuid = column("uuid", JDBCType.VARCHAR);

        public final SqlColumn<String> serverAddress = column("server_address", JDBCType.VARCHAR);

        public final SqlColumn<Integer> master = column("master", JDBCType.INTEGER);

        public final SqlColumn<Long> creationTime = column("creation_time", JDBCType.BIGINT);

        public final SqlColumn<Long> lastUpdateTime = column("last_update_time", JDBCType.BIGINT);

        public final SqlColumn<Long> terminationTime = column("termination_time", JDBCType.BIGINT);

        public WebserviceInfoRecord() {
            super("webservice_server_info");
        }
    }
}