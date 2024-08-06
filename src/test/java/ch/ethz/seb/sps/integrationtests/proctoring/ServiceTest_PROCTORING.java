package ch.ethz.seb.sps.integrationtests.proctoring;

import ch.ethz.seb.sps.domain.api.JSONMapper;
import com.fasterxml.jackson.core.type.TypeReference;
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

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

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
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = { "classpath:schema-test.sql", "classpath:proctoring-test-data.sql" })
public abstract class ServiceTest_PROCTORING {

    @Value("${sps.api.admin.gui.clientId}")
    protected String clientId;

    @Value("${sps.api.admin.gui.clientSecret}")
    protected String clientSecret;

    @Value("${sps.api.admin.endpoint}")
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



//    @Test
//    public void test() throws Exception {
//        this.mockMvc.perform(get("/homePage")).andDo(print());
//    }
//}


    protected String obtainAccessToken(final String username, final String password) throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
//        params.add("client_id", this.clientId);
        params.add("username", username);
        params.add("password", password);

        final ResultActions result = this.mockMvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic(this.clientId, this.clientSecret))
                .accept("application/json")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

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
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        }

        public <T> T getAsObject(final TypeReference<T> ref) throws Exception {
            final ResultActions action = ServiceTest_PROCTORING.this.mockMvc
                    .perform(requestBuilder());
            if (this.expectedStatus != null) {
                action.andExpect(status().is(this.expectedStatus.value()));
            }

            return ServiceTest_PROCTORING.this.jsonMapper.readValue(
                    action
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
