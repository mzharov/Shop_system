package test.aspect;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import test.config.RestClientConfig;
import test.config.TestDataServiceConfig;
import ts.tsc.system.controller.status.ErrorStatus;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static test.token.TokenFabric.getAccessToken;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {RestClientConfig.class, TestDataServiceConfig.class})
@WebAppConfiguration
public abstract class CreateTest<ENTITY> {

    final String clientID = "rest_client";
    final String secret = "secret";
    final String validUsername = "User1";
    final String validPassword = "password";

    static final String OAUTH_URL = "http://localhost:8080/oauth/token";

    @Autowired
    WebApplicationContext wac;

    @Autowired
    @SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection"})
    protected FilterChainProxy springSecurityFilterChain;

    MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilter(springSecurityFilterChain)
                .build();

        assertNotNull(wac);
        assertNotNull(mockMvc);
    }

    @Test
    public void testCreate() throws Exception{
        ENTITY entity = getEntity();

        Gson gson = new Gson();
        String json = gson.toJson(entity);

        ResultActions result
                = mockMvc.perform(post(getPath())
                .header("Authorization", "Bearer "
                        + getAccessToken(clientID, secret, validUsername, validPassword, mockMvc, OAUTH_URL))
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk());

        json = result.andReturn().getResponse().getContentAsString();
        validateEntity(json);
    }


    @Test
    public void testCreateWithID() throws Exception{
        ENTITY entity = getCorruptedEntity();

        Gson gson = new Gson();
        String json = gson.toJson(entity);

        ResultActions result
                = mockMvc.perform(post(getPath())
                .header("Authorization", "Bearer "
                        + getAccessToken(clientID, secret, validUsername, validPassword, mockMvc, OAUTH_URL))
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());

        json = result.andReturn().getResponse().getContentAsString();

        assertEquals("\"" + ErrorStatus.ID_CAN_NOT_BE_SET_IN_JSON.toString()+ "\"", json);
    }

    abstract String getPath();
    abstract ENTITY getEntity();
    abstract ENTITY getCorruptedEntity();
    abstract void validateEntity(String json) throws IOException;
}
