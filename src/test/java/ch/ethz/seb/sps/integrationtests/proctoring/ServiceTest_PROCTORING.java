package ch.ethz.seb.sps.integrationtests.proctoring;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import ch.ethz.seb.sps.server.ScreenProctoringServer;

@RunWith(SpringRunner.class)
@SpringBootTest(
        properties = "file.encoding=UTF-8",
        classes = { ScreenProctoringServer.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = { "classpath:schema-test.sql", "classpath:proctoring-test-data.sql" })
public abstract class ServiceTest_PROCTORING {


}
