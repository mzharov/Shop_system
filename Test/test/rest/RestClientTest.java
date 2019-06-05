package test.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.common.util.JacksonJsonParser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import test.config.RestClientConfig;
import test.config.TestDataServiceConfig;
import ts.tsc.system.entity.product.Product;

import java.util.*;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {RestClientConfig.class, TestDataServiceConfig.class})
@WebAppConfiguration
public class RestClientTest {

    private final static Logger logger = LoggerFactory.getLogger(RestClientTest.class);
    private static final String URL_GET_ALL_PRODUCTS = "http://localhost:8080/app/product/list";
    private static final String oAuthURL = "http://localhost:8080/oauth/token";

    @Value("${security.client-id}")
    private String clientID;
    @Value("${security.token.secret-key}")
    private String secret;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    @SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection"})
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilter(springSecurityFilterChain)
                .build();

        assertNotNull(wac);
        assertNotNull(mockMvc);
        assertNotNull(restTemplate);
    }

    @Test
    public void testFindAll() throws Exception{
        logger.info("Начало теста");

        MockHttpServletResponse response = obtainAccessToken(clientID,
                secret,
                "User1",
                "password");

        String resultString = response.getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        String accessToken = jsonParser.parseMap(resultString).get("access_token").toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Product>> shopResponseEntity =
                restTemplate.exchange(URL_GET_ALL_PRODUCTS,
                        HttpMethod.GET, entity,
                        new ParameterizedTypeReference<List<Product>>() {});
        assert shopResponseEntity != null;
        assert shopResponseEntity.getBody() != null;
        List<Product> shopList = shopResponseEntity.getBody();
        listShops(shopList);
    }

    private void listShops(List<Product> products) {

        products.forEach(s-> {
            logger.info("Product: ");
            logger.info("id: " + s.getId() + "; name: " + s.getName() + "; " + s.getCategory());
        });
    }

    @Test
    public void testUserAuth() throws Exception {
        MockHttpServletResponse response = obtainAccessToken(clientID,
                secret,
                "User1",
                "password");
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        System.out.print(response.getContentAsString() + " 1");
    }

    @Test
    public void testAdminAuth() throws Exception {
        MockHttpServletResponse response = obtainAccessToken(clientID,
                secret,
                "Admin",
                "admin");
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        System.out.print(response.getContentAsString());
    }

    private MockHttpServletResponse obtainAccessToken(String clientId,
                                                      String secret,
                                                      String username,
                                                      String password) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("grant_type", Collections.singletonList("password"));
        params.put("client_id", Collections.singletonList(clientId));
        params.put("username", Collections.singletonList(username));
        params.put("password", Collections.singletonList(password));
        params.put("client_secret", Collections.singletonList(secret));

        MockHttpServletResponse response;

        ResultActions result
                = mockMvc.perform(post(oAuthURL)
                .params(params)
                .with(httpBasic(clientId, secret))
                .accept("application/json;charset=UTF-8"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                ;


        response = result.andReturn().getResponse();
        return response;
    }

    private MockHttpServletResponse obtainRefreshToken(String clientId,
                                                       String secret,
                                                       String accessToken) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("grant_type", Collections.singletonList("refresh_token"));
        params.put("refresh_token", Collections.singletonList(accessToken));

        MockHttpServletResponse response;

        ResultActions result
                = mockMvc.perform(post(oAuthURL)
                .params(params)
                .with(httpBasic(clientId, secret))
                .accept("application/json;charset=UTF-8"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                ;

        response = result.andReturn().getResponse();
        return response;
    }

    @Test
    public void testRefreshToken() throws Exception {
        MockHttpServletResponse response = obtainAccessToken(clientID,
                secret,
                "Admin",
                "admin");
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        String resultString = response.getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        String refreshToken = jsonParser.parseMap(resultString).get("refresh_token").toString();
        assertNotNull(refreshToken);

        response = obtainRefreshToken(clientID, secret, refreshToken);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        System.out.print(response.getContentAsString());
    }
    @Test
    public void testBadRefreshToken() throws Exception {
        MockHttpServletResponse response = obtainAccessToken(clientID,
                secret,
                "Admin",
                "admin");
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        String resultString = response.getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        String refreshToken = jsonParser.parseMap(resultString).get("refresh_token").toString()+"1";
        assertNotNull(refreshToken);

        response = obtainRefreshToken(clientID, secret, refreshToken);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        System.out.print(response.getContentAsString());
    }
    @Test
    public void testBadCredentials() throws Exception {
        MockHttpServletResponse response = obtainAccessToken(
                clientID+1,
                secret,
                "Admin",
                "admin");
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }
    @Test
    public void testBadClientCredentials() throws Exception {
        MockHttpServletResponse response = obtainAccessToken(
                clientID+1,
                secret,
                "Admin",
                "admin");
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    public void testBadUserCredentials() throws Exception {
        MockHttpServletResponse response = obtainAccessToken(
                clientID,
                secret,
                "Admin1",
                "admin");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }
}