package test.aspect;

import com.google.gson.Gson;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import ts.tsc.system.controller.status.ErrorStatus;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static test.token.TokenFabric.getAccessToken;

/**
 * Абстрактный класс тестирования и обновления сущностей
 * @param <ENTITY>
 */
public abstract class CreateAndUpdateTest<ENTITY> extends CreateTest<ENTITY> {

    /**
     * Обновление с валидными данными
     */
    @Test
    public void testUpdate() throws Exception{
        ENTITY entity = getEntity();

        Gson gson = new Gson();
        String json = gson.toJson(entity);

        ResultActions result
                = mockMvc.perform(put(getUpdatePath())
                .header("Authorization", "Bearer "
                        + getAccessToken(clientID, secret, validUsername, validPassword, mockMvc, OAUTH_URL))
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk());

        json = result.andReturn().getResponse().getContentAsString();
        validateUpdated(json, getID());
    }

    /**
     * Обновление с переданным идентификатором в json параметре
     */
    @Test
    public void testUpdateWithID() throws Exception{
        ENTITY entity = getCorruptedEntity();

        Gson gson = new Gson();
        String json = gson.toJson(entity);

        ResultActions result
                = mockMvc.perform(put(getUpdatePath())
                .header("Authorization", "Bearer "
                        + getAccessToken(clientID, secret, validUsername, validPassword, mockMvc, OAUTH_URL))
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());

        json = result.andReturn().getResponse().getContentAsString();

        assertEquals("\"" + ErrorStatus.ID_CAN_NOT_BE_SET_IN_JSON.toString()+ "\"", json);
    }

    abstract void validateUpdated(String json, Long id) throws IOException;
    @SuppressWarnings("SameReturnValue")
    abstract Long getID();
    private String getUpdatePath() {
        return getPath()+getID();
    }
}
