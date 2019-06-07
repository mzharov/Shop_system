package test.token;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.common.util.JacksonJsonParser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class TokenFabric {
    /**
     * Запрос токена
     * @param clientId id клиента
     * @param secret секрет клиента
     * @param username имя пользователя
     * @param password пароль пользователя
     */
    public static MockHttpServletResponse obtainAccessToken(String clientId,
                                                      String secret,
                                                      String username,
                                                      String password,
                                                      MockMvc mockMvc,
                                                      String OAUTH_URL) throws Exception {
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
                .andExpect(content().contentType("application/json;charset=UTF-8"));


        response = result.andReturn().getResponse();
        return response;
    }

    /**
     * Запрос токена на основе токена обновления
     * @param clientId идентификатор клиента
     * @param secret секрет клиента
     * @param refreshToken токен обновления
     */
     public static MockHttpServletResponse obtainAccessTokenByRefreshToken(String clientId,
                                                                    String secret,
                                                                    String refreshToken,
                                                                    MockMvc mockMvc,
                                                                    String OAUTH_URL) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("grant_type", Collections.singletonList("refresh_token"));
        params.put("refresh_token", Collections.singletonList(refreshToken));

        MockHttpServletResponse response;

        ResultActions result
                = mockMvc.perform(post(OAUTH_URL)
                .params(params)
                .with(httpBasic(clientId, secret))
                .accept("application/json;charset=UTF-8"))
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        response = result.andReturn().getResponse();
        return response;
    }

    /**
     * Формирование заголовка HTTP с токеном
     * @param username имя пользователя
     * @param password пароль пользователя
     */
    public static HttpEntity<String> getHeaderWithAccessToken(String clientID,
                                                       String secret,
                                                       String username,
                                                       String password,
                                                       MockMvc mockMvc,
                                                       String OAUTH_URL) throws Exception {
        String accessToken = getAccessToken(clientID, secret, username, password, mockMvc, OAUTH_URL);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+accessToken);
        return new HttpEntity<>(headers);
    }

    public static String getAccessToken(String clientID,
                                        String secret,
                                        String username,
                                        String password,
                                        MockMvc mockMvc,
                                        String OAUTH_URL) throws Exception {
        MockHttpServletResponse response = obtainAccessToken(
                clientID,
                secret,
                username,
                password, mockMvc,
                OAUTH_URL);

        String resultString = response.getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }
}
