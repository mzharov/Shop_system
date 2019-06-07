package test.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.Gson;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import test.deserializer.ShopStorageDeserializer;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.entity.shop.ShopStorage;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static test.token.TokenFabric.getAccessToken;

public class ShopStorageCreateTest extends CreateTest<ShopStorage> {

    private final static String PATH = "http://localhost:8080/app/shop/storage/1";

    @Override
    String getPath() {
        return PATH;
    }

    @Override
    ShopStorage getEntity() {
        ShopStorage shopStorage = new ShopStorage();
        shopStorage.setType(0);
        shopStorage.setTotalSpace(1);
        shopStorage.setFreeSpace(1);
        return shopStorage;
    }

    @Override
    ShopStorage getCorruptedEntity() {
        ShopStorage shopStorage = new ShopStorage();
        shopStorage.setId(1L);
        shopStorage.setType(0);
        shopStorage.setTotalSpace(1);
        shopStorage.setFreeSpace(1);
        return shopStorage;
    }

    @Override
    void validateEntity(String json) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(ShopStorage.class, new ShopStorageDeserializer());
        objectMapper.registerModule(simpleModule);

        ShopStorage shopStorage = objectMapper.readValue(json, ShopStorage.class);
        assertNotNull(shopStorage);
        assertEquals(shopStorage.getType(), shopStorage.getType());
        assertEquals(shopStorage.getTotalSpace(), shopStorage.getTotalSpace());
        assertEquals(shopStorage.getFreeSpace(), shopStorage.getFreeSpace());
    }

    private ShopStorage getStorageWithDuplicateMain() {
        ShopStorage shopStorage = new ShopStorage();
        shopStorage.setType(1);
        shopStorage.setTotalSpace(1);
        shopStorage.setFreeSpace(1);
        return shopStorage;
    }

    @Test
    public void testCreateShopWithID() throws Exception{
        ShopStorage entity = getStorageWithDuplicateMain();

        Gson gson = new Gson();
        String json = gson.toJson(entity);

        ResultActions result
                = mockMvc.perform(post(PATH)
                .header("Authorization", "Bearer "
                        + getAccessToken(clientID, secret, validUsername, validPassword, mockMvc, OAUTH_URL))
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());

        json = result.andReturn().getResponse().getContentAsString();

        assertEquals("\"" + ErrorStatus.MAIN_STORAGE_ALREADY_EXISTS.toString()+ "\"", json);
    }
}
