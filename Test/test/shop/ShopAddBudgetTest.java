package test.shop;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import test.config.TestDataServiceConfig;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.service.shop.ShopInterface;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Тестирование добавление денег в бюджет магазина
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {TestDataServiceConfig.class})
@WebAppConfiguration
public class ShopAddBudgetTest {

    private final Logger logger = LoggerFactory.getLogger(ShopAddBudgetTest.class);

    @Autowired
    ShopInterface shopService;

    /**
     * Проверка на валидных данных
     */
    @Test
    public void addBudgetTest() {
        logger.info("Добавление денег в бюджет магазина");
        Long shopID = 1L;
        Optional<Shop> shopOptional = shopService.findById(shopID);
        assertTrue(shopOptional.isPresent());
        Shop shop = shopOptional.get();
        BigDecimal oldBudget = shop.getBudget();
        String money = "1000.9990";
        shopService.addBudget(1L, money);

        shopOptional = shopService.findById(shopID);
        assertTrue(shopOptional.isPresent());
        shop = shopOptional.get();
        BigDecimal newBudget = shop.getBudget();
        assertEquals(oldBudget.add(new BigDecimal(money)), newBudget);
    }

    /**
     * Проверка на неверных данных
     */
    @Test
    public void addBudgetNumberFormatExceptionTest() {
        logger.info("Добавление денег в бюджет магазина");
        Long shopID = 1L;
        Optional<Shop> shopOptional = shopService.findById(shopID);
        assertTrue(shopOptional.isPresent());
        Shop shop = shopOptional.get();
        BigDecimal oldBudget = shop.getBudget();
        String money1 = "1000,9990";
        ResponseEntity<?> responseEntity = shopService.addBudget(1L, money1);
        assertEquals(ErrorStatus.NUMBER_FORMAT_EXCEPTION, responseEntity.getBody());

        shopOptional = shopService.findById(shopID);
        assertTrue(shopOptional.isPresent());
        shop = shopOptional.get();
        BigDecimal newBudget = shop.getBudget();
        assertEquals(oldBudget, newBudget);
    }
}
