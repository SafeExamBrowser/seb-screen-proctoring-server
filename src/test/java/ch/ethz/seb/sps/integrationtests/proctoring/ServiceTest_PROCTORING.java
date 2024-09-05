package ch.ethz.seb.sps.integrationtests.proctoring;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.service.ScreenshotData;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.user.ServerUser;
import ch.ethz.seb.sps.domain.model.user.UserInfo;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.server.datalayer.dao.UserDAO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import ch.ethz.seb.sps.server.ScreenProctoringServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.ServletContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest(
        properties = "file.encoding=UTF-8",
        classes = { ScreenProctoringServer.class },
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = { "classpath:schema-test.sql", "classpath:proctoring-test-data.sql" })
public abstract class ServiceTest_PROCTORING {

    @Value("${sps.api.admin.gui.clientId}")
    protected String clientId;

    @Value("${sps.api.admin.gui.clientSecret}")
    protected String clientSecret;

    @Value("${sps.api.admin.endpoint.v1}")
    protected String endpoint;


    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected JSONMapper jsonMapper;

    @Autowired
    protected FilterChainProxy springSecurityFilterChain;

    protected MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(this.springSecurityFilterChain).build();
    }

    protected String obtainAccessToken(final String username, final String password) throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", username);
        params.add("password", password);

        final ResultActions result = this.mockMvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic(this.clientId, this.clientSecret))
                .accept("application/json;charset=UTF-8")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        final String resultString = result.andReturn().getResponse().getContentAsString();

        final JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    protected String getSebAdminAccess() throws Exception {
        return obtainAccessToken("super-admin", "admin");
    }

    protected class RestAPITestHelper {

        private String path = "";
        private final Map<String, String> queryAttrs = new HashMap<>();
        private String accessToken;
        private HttpStatus expectedStatus;
        private HttpMethod httpMethod = HttpMethod.POST;
        private MediaType contentType = MediaType.APPLICATION_FORM_URLENCODED;
        private String body = null;

        public RestAPITestHelper withAccessToken(final String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public RestAPITestHelper withPath(final String path) {
            if (path == null) {
                return this;
            }
            this.path += (path.startsWith("/")) ? path : "/" + path;
            return this;
        }

        public RestAPITestHelper withAttribute(final String name, final String value) {
            this.queryAttrs.put(name, value);
            return this;
        }

        public RestAPITestHelper withAttributes(Map<String, String> attributes) {
            for (Map.Entry<String, String> attribute : attributes.entrySet()) {
                this.queryAttrs.put(attribute.getKey(), attribute.getValue());
            }

            return this;
        }

        public RestAPITestHelper withExpectedStatus(final HttpStatus expectedStatus) {
            this.expectedStatus = expectedStatus;
            return this;
        }

        public RestAPITestHelper withMethod(final HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public RestAPITestHelper withBodyJson(final Object object) throws Exception {
            this.contentType = MediaType.APPLICATION_JSON;
            this.body = ServiceTest_PROCTORING.this.jsonMapper.writeValueAsString(object);
            return this;
        }

        public void checkStatus() throws Exception {
            this.getAsString();
        }

        public String getAsString() throws Exception {
            final ResultActions action = ServiceTest_PROCTORING.this.mockMvc
                    .perform(requestBuilder());

            if (this.expectedStatus != null) {
                action.andExpect(status().is(this.expectedStatus.value()));
            }

            return action
                    .andDo(print())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        }

        public <T> T getAsObject(final TypeReference<T> ref) throws Exception {
            final ResultActions action = ServiceTest_PROCTORING.this.mockMvc
                    .perform(requestBuilder());
            if (this.expectedStatus != null) {
                action
                        .andDo(print())
                        .andExpect(status().is(this.expectedStatus.value()));
            }

            return ServiceTest_PROCTORING.this.jsonMapper.readValue(
                    action
                            .andDo(print())
                            .andReturn()
                            .getResponse()
                            .getContentAsString(),
                    ref);
        }

        private RequestBuilder requestBuilder() {
            MockHttpServletRequestBuilder builder = get(getFullPath());
            switch (this.httpMethod) {
                case GET:
                    builder = get(getFullPath());
                    break;
                case POST:
                    builder = post(getFullPath());
                    break;
                case PUT:
                    builder = put(getFullPath());
                    break;
                case DELETE:
                    builder = delete(getFullPath());
                    break;
                case PATCH:
                    builder = patch(getFullPath());
                    break;
                default:
                    get(getFullPath());
                    break;
            }
            builder.header("Authorization", "Bearer " + this.accessToken);

            if (this.contentType != null) {
                builder.contentType(this.contentType);
            }
            if (this.body != null) {
                builder.content(this.body);
            }

            return builder;
        }

        private String getFullPath() {
            final StringBuilder sb = new StringBuilder();
            sb.append(ServiceTest_PROCTORING.this.endpoint);
            sb.append(this.path);
            if (!this.queryAttrs.isEmpty()) {
                sb.append("?");
                this.queryAttrs.entrySet()
                        .stream()
                        .reduce(
                                sb,
                                (buffer, entry) -> buffer.append(entry.getKey()).append("=").append(entry.getValue())
                                        .append("&"),
                                (sb1, sb2) -> sb1.append(sb2));
                sb.deleteCharAt(sb.length() - 1);
            }
            return sb.toString();
        }
    }



}
