package test.shop;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import test.config.TestDataServiceConfig;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.service.shop.ShopInterface;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {TestDataServiceConfig.class})
@WebAppConfiguration
public class ShopBasicMethodsTest {

    private final Logger logger = LoggerFactory.getLogger(ShopBasicMethodsTest.class);

    @Autowired
    ShopInterface shopService;

    @Test
    public void findAll() {
        logger.info("Список всех магазинов");
        List<Shop> shopList = shopService.findAll();
        assert shopList !=null;
        assertNotNull(shopList);
        assertEquals(2, shopList.size());
    }

    @Test
    public void findById() {
        logger.info("Поиск магазина по идентификатору");
        Optional<Shop> shopOptional = shopService.findById(1L);
        assertTrue(shopOptional.isPresent());
    }
}
