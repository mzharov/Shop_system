package test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.entity.shop.ShopStorage;
import ts.tsc.system.entity.shop.ShopStorageProduct;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {RestClientConfig.class})
public class RestClientTest {

    private final static Logger logger = LoggerFactory.getLogger(RestClientTest.class);
    private static final String URL_GET_ALL_SHOPS = "http://localhost:8080/shop/list";

    @Autowired
    RestTemplate restTemplate;

    @Before
    public void setUp() {
        assertNotNull(restTemplate);
    }

    @Test
    public void testFindAll() {
        logger.info("Начало теста");
        ResponseEntity<List<Shop>> shopResponseEntity =
                restTemplate.exchange(URL_GET_ALL_SHOPS,
                        HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<Shop>>() {});
        assert shopResponseEntity != null;
        assert shopResponseEntity.getBody() != null;
        List<Shop> shopList = shopResponseEntity.getBody();
        assertEquals(2, shopList.size());
        listShops(shopList);
    }

    private void listShops(List<Shop> shops) {
        shops.forEach(s -> {
            logger.info("Shop:");
            logger.info("id: " + s.getId() + "; name: " + s.getName() + "; budget: " + s.getBudget());
            Set<ShopStorage> shopStorageSet = s.getStorages();
            logger.info("Storages:");
            shopStorageSet.forEach(shopStorage -> {
                logger.info("id: " + shopStorage.getId() + "; type: "
                        + shopStorage.getType() + "; totalSpace: " +
                        shopStorage.getTotalSpace() + "; freeSpace: "
                        + shopStorage.getFreeSpace());
                Set<ShopStorageProduct> shopStorageProductSet = shopStorage.getProducts();
                logger.info("Products: " + shopStorageProductSet.size());
                shopStorageProductSet.forEach(shopStorageProduct ->
                    logger.info("count: " + shopStorageProduct.getCount()
                            + "; price: " + shopStorageProduct.getPrice()));
        });
        });
    }
}