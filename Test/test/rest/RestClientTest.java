package test.rest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import test.config.RestClientConfig;
import test.config.TestDataServiceConfig;
import ts.tsc.authentication.entity.User;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.service.product.ProductServiceInterface;

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
    private static final String URL_GET_ALL_USERS = "http://localhost:8080/user/list";
    private static final String OAUTH_URL = "http://localhost:8080/oauth/token";

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

    @Autowired
    ProductServiceInterface productService;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilter(springSecurityFilterChain)
                .build();

        assertNotNull(wac);
        assertNotNull(mockMvc);
        assertNotNull(restTemplate);
    }

    /**
     * Тест запроса списка товаров авторизованным пользователем
     */
    @Test
    public void testFindAllProductsByAuthorizedUser() throws Exception{
        logger.info("Начало теста");

        ResponseEntity<List<Product>> productResponseEntity =
                restTemplate.exchange(URL_GET_ALL_PRODUCTS,
                        HttpMethod.GET, getHeaderWithAccessToken("User1", "password"),
                        new ParameterizedTypeReference<List<Product>>() {});
        assert productResponseEntity != null;
        assert productResponseEntity.getBody() != null;
        List<Product> productList = productResponseEntity.getBody();
        printProducts(productList);
    }

    /**
     * Тест запроса списка товаров неавторзованным пользователем,
     * должно быть сгенерировано исключение с кодом 401
     */
    @Test
    public void testFindAllProductsByUnauthorizedUser() {
        logger.info("Начало теста");

        expectedException.expect(HttpClientErrorException.class);
        expectedException.expectMessage("401");

        restTemplate.exchange(URL_GET_ALL_PRODUCTS,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Product>>() {});
    }

    private void printProducts(List<Product> products) {
        products.forEach(s-> {
            System.out.println("Product: ");
            System.out.println("id: " + s.getId() + "; name: " + s.getName() + "; " + s.getCategory());
        });
    }

    /**
     * Тест авторизации существующего пользователя
     */
    @Test
    public void testUserAuth() throws Exception {
        MockHttpServletResponse response = obtainAccessToken(clientID,
                secret,
                "User1",
                "password");
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        System.out.print(response.getContentAsString() + " 1");
    }

    /**
     * Тест авторизации админа
     */
    @Test
    public void testAdminAuth() throws Exception {
        MockHttpServletResponse response = obtainAccessToken(clientID,
                secret,
                "Admin",
                "admin");
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        System.out.print(response.getContentAsString());
    }

    /**
     * Запрос токена
     * @param clientId id клиента
     * @param secret секрет клиента
     * @param username имя пользователя
     * @param password пароль пользователя
     */
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
                = mockMvc.perform(post(OAUTH_URL)
                .params(params)
                .with(httpBasic(clientId, secret))
                .accept("application/json;charset=UTF-8"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                ;


        response = result.andReturn().getResponse();
        return response;
    }

    /**
     * Формирование заголовка HTTP с токеном
     * @param username имя пользователя
     * @param password пароль пользователя
     */
    private HttpEntity<String> getHeaderWithAccessToken(String username,
                                                        String password) throws Exception {
        MockHttpServletResponse response = obtainAccessToken(clientID,
                secret,
                username,
                password);

        String resultString = response.getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        String accessToken = jsonParser.parseMap(resultString).get("access_token").toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+accessToken);
        return new HttpEntity<>(headers);
    }

    /**
     * Запрос токена на основе токена обновления
     * @param clientId идентификатор клиента
     * @param secret секрет клиента
     * @param refreshToken токен обновления
     */
    private MockHttpServletResponse obtainAccessTokenByRefreshToken(String clientId,
                                                       String secret,
                                                       String refreshToken) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("grant_type", Collections.singletonList("refresh_token"));
        params.put("refresh_token", Collections.singletonList(refreshToken));

        MockHttpServletResponse response;

        ResultActions result
                = mockMvc.perform(post(OAUTH_URL)
                .params(params)
                .with(httpBasic(clientId, secret))
                .accept("application/json;charset=UTF-8"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                ;

        response = result.andReturn().getResponse();
        return response;
    }


    /**
     * Тест запроса токена на основе токена обновления
     */
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

        response = obtainAccessTokenByRefreshToken(clientID, secret, refreshToken);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        System.out.print(response.getContentAsString());
    }

    /**
     * Тест запроса токена обновления с неверным токеном обновления
     */
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

        response = obtainAccessTokenByRefreshToken(clientID, secret, refreshToken);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        System.out.print(response.getContentAsString());
    }

    /**
     * Тест запроса токена на основе неверных данными
     */
    @Test
    public void testBadCredentials() throws Exception {
        MockHttpServletResponse response = obtainAccessToken(
                clientID+1,
                secret,
                "Admin",
                "admin");
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }


    /**
     * Тест запроса токена на основе неверного логина/пароля
     */
    @Test
    public void testBadUserCredentials() throws Exception {
        MockHttpServletResponse response = obtainAccessToken(
                clientID,
                secret,
                "Admin1",
                "admin");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    /**
     * Тест запроса списка пользовтаетелй администатором
     */
    @Test
    public void testUserListAccessByAdmin() throws Exception {
        logger.info("Начало теста");

        MockHttpServletResponse response = obtainAccessToken(clientID,
                secret,
                "Admin",
                "admin");

        assertNotNull(response);

        ResponseEntity<List<User>> userResponseEntity = restTemplate.exchange(URL_GET_ALL_USERS,
                        HttpMethod.GET, getHeaderWithAccessToken("Admin", "admin"),
                        new ParameterizedTypeReference<List<User>>() {});

        assert userResponseEntity != null;
        assert userResponseEntity.getBody() != null;
        List<User> userList = userResponseEntity.getBody();
        printUsers(userList);
    }

    /**
     * Тест запроса списка пользователей обычным пользователем,
     * должно быть сгенерировано исключения с кодом 403
     */
    @Test
    public void testUserListAccessByUser() throws Exception {
        logger.info("Начало теста");

        expectedException.expect(HttpClientErrorException.class);
        expectedException.expectMessage("403");

        restTemplate.exchange(URL_GET_ALL_USERS,
                HttpMethod.GET, getHeaderWithAccessToken("User1", "password"),
                new ParameterizedTypeReference<List<User>>() {});
    }

    private void printUsers(List<User> products) {
        products.forEach(s-> {
            System.out.println("Product: ");
            System.out.println("id: " + s.getId() + "; name: " + s.getName() + "; " + s.getRoles());
        });
    }

    @Test
    @SqlGroup({@Sql(value = "classpath:db/test-sql.sql",
            config = @SqlConfig(encoding = "utf-8",
                    separator = ";",
                    commentPrefix = "--"),
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/testSqlClean.sql",
                    config = @SqlConfig(encoding = "utf-8",
                            separator = ";",
                            commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)})
    public  void testGetAllProducts() {
        List<Product> productList = productService.findAll();
        printProducts(productList);
    }
}