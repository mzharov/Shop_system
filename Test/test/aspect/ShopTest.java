package test.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import test.deserializer.ShopStorageDeserializer;
import test.deserializer.ShopStorageProductDeserializer;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.entity.shop.ShopStorage;
import ts.tsc.system.entity.shop.ShopStorageProduct;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ShopTest extends CreateAndUpdateTest<Shop>{

    private final static String PATH = "http://localhost:8080/app/shop/";

    @Override
    String getPath() {
        return PATH;
    }

    @Override
    Shop getEntity() {
        Shop shop = new Shop();
        shop.setName("name");
        shop.setBudget(new BigDecimal(10));
        return shop;
    }

    @Override
    Shop getCorruptedEntity() {
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("name");
        shop.setBudget(new BigDecimal(10));
        return shop;
    }

    @Override
    void validateEntity(String json) throws IOException {
        Shop shop = new ObjectMapper().readValue(json, Shop.class);
        assertNotNull(shop);
        assertEquals(shop.getName(), shop.getName());
        assertEquals(shop.getBudget(), shop.getBudget());
    }

    @Override
    void validateUpdated(String json, Long id) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(ShopStorage.class, new ShopStorageDeserializer());
        simpleModule.addDeserializer(ShopStorageProduct.class, new ShopStorageProductDeserializer());
        objectMapper.registerModule(simpleModule);

        Shop shop = objectMapper.readValue(json, Shop.class);
        assertNotNull(shop);
        assertEquals(id, shop.getId());
        assertEquals(shop.getName(), shop.getName());
        assertEquals(shop.getBudget(), shop.getBudget());
    }

    @Override
    Long getID() {
        return 1L;
    }
}
