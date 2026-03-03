package ch.ethz.seb.sps.integrationtests;

import ch.ethz.seb.sps.domain.api.JSONMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestAPITestHelper {

    private final String endpoint;
    private final JSONMapper jsonMapper;
    private final MockMvc mockMvc;

    private String path = "";
    private final Map<String, String> queryAttrs = new HashMap<>();
    private String accessToken;
    private HttpStatus expectedStatus;
    private HttpMethod httpMethod = POST;
    private MediaType contentType = MediaType.APPLICATION_FORM_URLENCODED;
    private String body = null;

    public RestAPITestHelper(
            final String endpoint,
            final JSONMapper jsonMapper,
            final MockMvc mockMvc) {

        this.endpoint = endpoint;
        this.jsonMapper = jsonMapper;
        this.mockMvc = mockMvc;
    }


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
        this.body = jsonMapper.writeValueAsString(object);
        return this;
    }

    public void checkStatus() throws Exception {
        this.getAsString();
    }

    public String getAsString() throws Exception {
        final ResultActions action = mockMvc
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
        final ResultActions action = mockMvc
                .perform(requestBuilder());
        if (this.expectedStatus != null) {
            action
                    .andDo(print())
                    .andExpect(status().is(this.expectedStatus.value()));
        }

        String contentAsString = action
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println("********** Content as String: " +contentAsString );

        return jsonMapper.readValue(contentAsString, ref);
    }

    private RequestBuilder requestBuilder() {
        MockHttpServletRequestBuilder builder = get(getFullPath());
        if (this.httpMethod.equals(GET)) {
            builder = get(getFullPath());
        } else if (this.httpMethod.equals(POST)) {
            builder = post(getFullPath());
        } else if (this.httpMethod.equals(PUT)) {
            builder = put(getFullPath());
        } else if (this.httpMethod.equals(DELETE)) {
            builder = delete(getFullPath());
        } else if (this.httpMethod.equals(PATCH)) {
            builder = patch(getFullPath());
        } else {
            get(getFullPath());
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
        sb.append(endpoint);
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
