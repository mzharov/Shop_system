package test.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.JacksonJsonParser;
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
import ts.tsc.authentication.entity.User;
import ts.tsc.authentication.error.UserError;
import ts.tsc.authentication.repository.UserRepository;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static test.token.TokenFabric.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {RestClientConfig.class, TestDataServiceConfig.class})
@WebAppConfiguration
public class ChangePasswordTest {

    @Value("${security.client-id}")
    String clientID;
    @Value("${security.token.secret-key}")
    String secret;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    @SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection"})
    protected FilterChainProxy springSecurityFilterChain;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private final String GET_PRODUCT_LIST = "http://localhost:8080/app/product/list";
    private static final String OAUTH_URL = "http://localhost:8080/oauth/token";

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilter(springSecurityFilterChain)
                .build();

        assertNotNull(wac);
        assertNotNull(mockMvc);
    }

    @Test
    public void changePasswordWithValidCredentials() throws Exception {

       String firstUsername = "User1";
       String firstUserValidPassword = "password";
       String firstUserNewPassword = "pass";
       String CHANGE_VALID_PASSWORD_URL
               = "http://localhost:8080/user/"+firstUserValidPassword+"/"+firstUserNewPassword;


        MockHttpServletResponse response = obtainAccessToken(clientID,
                secret,
                firstUsername,
                firstUserValidPassword, mockMvc, OAUTH_URL);
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        String resultString = response.getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        String accessToken = jsonParser.parseMap(resultString).get("access_token").toString();
        String refreshToken = jsonParser.parseMap(resultString).get("refresh_token").toString();
        assertNotNull(refreshToken);
        assertNotNull(accessToken);

        ResultActions result
                = mockMvc.perform(put(CHANGE_VALID_PASSWORD_URL)
                .header("Authorization", "Bearer " + accessToken)
                .accept("application/json;charset=UTF-8"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk());
        String json = result.andReturn().getResponse().getContentAsString();
        User user = new ObjectMapper().readValue(json, User.class);
        assertNotNull(user);
        assertEquals(firstUsername, user.getName());

        Optional<User> userOptional = userRepository.findUserByName(user.getName());
        assertTrue(userOptional.isPresent());
        user = userOptional.get();
        assertTrue(passwordEncoder.matches(firstUserNewPassword, user.getPassword()));

        mockMvc.perform(get(GET_PRODUCT_LIST)
                .header("Authorization", "Bearer " + accessToken)
                .accept("application/json;charset=UTF-8"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isUnauthorized());


        response = obtainAccessTokenByRefreshToken(clientID, secret, refreshToken, mockMvc, OAUTH_URL);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void changePasswordWithInvalidCredentials() throws Exception {
        String secondUsername = "Admin";
        String secondUserValidPassword = "admin";
        String CHANGE_INVALID_PASSWORD_URL = "http://localhost:8080/user/password/pass";

        MockHttpServletResponse response = obtainAccessToken(clientID,
                secret,
                secondUsername,
                secondUserValidPassword, mockMvc, OAUTH_URL);
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        String resultString = response.getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        String accessToken = jsonParser.parseMap(resultString).get("access_token").toString();
        String refreshToken = jsonParser.parseMap(resultString).get("refresh_token").toString();
        assertNotNull(accessToken);

        ResultActions result
                = mockMvc.perform(put(CHANGE_INVALID_PASSWORD_URL)
                .header("Authorization", "Bearer " + accessToken)
                .accept("application/json;charset=UTF-8"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
        String json = result.andReturn().getResponse().getContentAsString();
        assertNotNull(json);
        assertEquals("\""+UserError.INVALID_PASSWORD.toString()+"\"", json);

        mockMvc.perform(get(GET_PRODUCT_LIST)
                .header("Authorization", "Bearer " + accessToken)
                .accept("application/json;charset=UTF-8"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk());

        response = obtainAccessTokenByRefreshToken(clientID, secret, refreshToken, mockMvc, OAUTH_URL);
        assertEquals(HttpStatus.OK.value(), response.getStatus());

    }

}
