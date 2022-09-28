package ch.ethz.seb.sps.generator;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.h2.jdbcx.JdbcDataSource;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class SPSModelGenerator {

    private static final String SCHEMA_VERSION_ENV_KEY = "SCHEMA_VERSION";

    private static final Logger log = LoggerFactory.getLogger(SPSModelGenerator.class);

    private static final String CONFIG_RESOURCE_PATH = "/ch/ethz/seb/sps/gen/generatorConfig.xml";
    private static final String USER_NAME = "SPSModelGenerator";

    private static final String SCHEMA_NAME = "gen";
    private static final String SCHEMA_URL_PREFIX = "'classpath:/ch/ethz/seb/sps/schema/";
    private static final String SCHEMA_FILE_NAME = "schema-h2.sql";
    private static final String DB_URL = "jdbc:h2:mem:" + SCHEMA_NAME;

    public static void main(final String[] args) throws Exception {

        final String getenv = System.getProperty(SCHEMA_VERSION_ENV_KEY);
        final String url = SCHEMA_URL_PREFIX + "version_" + getenv.replace(".", "_") + "/" + SCHEMA_FILE_NAME + "'";
        final String INIT_SCRIPT = ";runscript from " + url;
        final String INIT = ";INIT=create schema if not exists " + SCHEMA_NAME + "\\" + INIT_SCRIPT;
        final String MODE = ";MODE=MySQL;DATABASE_TO_UPPER=false";

        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(DB_URL + INIT + MODE);
        ds.setUser(USER_NAME);
        final Connection conn = ds.getConnection();

        log.info("Connection: " + conn);

        final List<String> warnings = new ArrayList<>();
        final boolean overwrite = true;
        final Resource resource = new ClassPathResource(CONFIG_RESOURCE_PATH);
        final ConfigurationParser cp = new ConfigurationParser(warnings);
        final Configuration config = cp.parseConfiguration(resource.getFile());
        final DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        final MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }

}
