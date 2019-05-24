package test;

import test.config.TestDataServiceConfig;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.controller.status.OrderStatus;
import ts.tsc.system.entity.purchase.Purchase;
import ts.tsc.system.entity.purchase.PurchaseProduct;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.entity.shop.ShopStorage;
import ts.tsc.system.repository.shop.ShopStorageRepository;
import ts.tsc.system.service.base.BaseServiceInterface;
import ts.tsc.system.service.shop.ShopInterface;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {TestDataServiceConfig.class})
@WebAppConfiguration
public class ShopServiceTest{

    private final static Logger logger = LoggerFactory.getLogger(ShopServiceTest.class);

    @Autowired
    public ShopInterface shopService;
    @Autowired
    public BaseServiceInterface<Purchase, Long> purchaseService;
    @Autowired
    public ShopStorageRepository shopStorageRepository;

    @BeforeClass
    public static void SetUp() {
        logger.info("Начало теста ShopService");
    }

    @AfterClass
    public static void tearDown() {
        logger.info("Конец теста ShopService");
    }


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

    @Test
    public void receiveAndDeliverAndCompleteOrderTest() {
        logger.info("Заказ, доставка и завершение заказа");
        Long shopID = 1L;
        List<Long> productIDList = Arrays.asList(1L, 2L);
        List<Integer> countList = Arrays.asList(10, 10);
        Object[] params= receiveOrder(shopID, productIDList, countList);
        Long purchaseID = (Long) params[0];
        BigDecimal oldBudget = (BigDecimal) params[1];
        BigDecimal purchaseSumPrice = (BigDecimal) params[2];
        deliverOrder(purchaseID);
        completeOrder(purchaseID);
        shopBudgetTest(shopID, oldBudget, purchaseSumPrice);
    }

    @Test
    public void receiveAndCancelOrderTest() {
        logger.info("Заказ и отмена товара");
        Long shopID = 1L;
        List<Long> productIDList = Arrays.asList(1L, 2L);
        List<Integer> countList = Arrays.asList(10, 10);
        Object[] params= receiveOrder(shopID, productIDList, countList);
        Long purchaseID = (Long) params[0];
        BigDecimal oldBudget = (BigDecimal) params[1];
        cancelOrder(purchaseID);
        shopBudgetTest(shopID, oldBudget, new BigDecimal(0));
    }
    @Test
    public void ReceiveAndDeliverAndCancelOrderTest() {
        logger.info("Заказ, доставка и отмена заказа");
        Long shopID = 1L;
        List<Long> productIDList = Arrays.asList(1L, 2L);
        List<Integer> countList = Arrays.asList(10, 10);
        Object[] params= receiveOrder(shopID, productIDList, countList);
        Long purchaseID = (Long) params[0];
        BigDecimal oldBudget = (BigDecimal) params[1];
        deliverOrder(purchaseID);
        cancelOrder(purchaseID);
        shopBudgetTest(shopID, oldBudget, new BigDecimal(0));
    }


    private Object[] receiveOrder(Long shopID, List<Long> productIDList, List<Integer> countList) {
        Optional<Shop> shopOptional = shopService.findById(shopID);
        assertTrue(shopOptional.isPresent());
        Shop shop = shopOptional.get();
        BigDecimal oldBudget = shop.getBudget();
        int oldFreeSpace = getFreeSpaceInMainStorage(shopID);

        ResponseEntity<?> responseEntity = shopService
                .receiveOrder(shopID, Arrays.asList(1L,2L), Arrays.asList(10,10));
        assert responseEntity.getStatusCode().equals(HttpStatus.OK);
        Purchase purchase = (Purchase) responseEntity.getBody();
        assertNotNull(purchase);
        assert purchase.getOrderStatus().equals(OrderStatus.RECEIVED);
        Optional<Purchase> purchaseOptional = purchaseService.findById(purchase.getId());
        assert purchaseOptional.isPresent();
        purchase = purchaseOptional.get();
        assertNotNull(purchase.getPurchaseProducts());
        Set<PurchaseProduct> purchaseProductSet = purchase.getPurchaseProducts();
        assertNotNull(purchaseProductSet);

        List<PurchaseProduct> purchaseProductList = purchaseProductSet.stream()
                .sorted(Comparator.comparing(item->item.getPrimaryKey().getProduct().getId()))
                .collect(Collectors.toList());

        for (int iterator =0; iterator <  purchaseProductList.size(); iterator++) {
            assertEquals(purchaseProductList.get(iterator).getCount(),
                    (int) countList.get(iterator));
            assertEquals(purchaseProductList.get(iterator).getPrimaryKey().getProduct().getId(),
                    productIDList.get(iterator));
            iterator++;
        }

        BigDecimal purchaseSumPrice = new BigDecimal(0);
        int purchaseProductCount = 0;
        for(PurchaseProduct purchaseProduct : purchaseProductList) {
            purchaseProductCount +=purchaseProduct.getCount();
            purchaseSumPrice = purchaseSumPrice.add(purchaseProduct.getSumPrice());
        }

        Optional<Shop> updatedShop = shopService.findById(shopID);
        assertTrue(updatedShop.isPresent());
        int newFreeSpace = getFreeSpaceInMainStorage(shopID);
        assertEquals(newFreeSpace, oldFreeSpace+purchaseProductCount);

        return new Object[]{purchase.getId(), oldBudget, purchaseSumPrice};
    }

    public void deliverOrder(Long id) {
        shopService.deliverOrder(id);
        Optional<Purchase> purchaseOptional = purchaseService.findById(id);
        assertTrue(purchaseOptional.isPresent());
        assertEquals(OrderStatus.DELIVERING, purchaseOptional.get().getOrderStatus());
    }
    private void completeOrder(Long id) {
        shopService.completeOrder(id);
        Optional<Purchase> purchaseOptional = purchaseService.findById(id);
        assertTrue(purchaseOptional.isPresent());
        assertEquals(OrderStatus.COMPLETED, purchaseOptional.get().getOrderStatus());
    }
    private void cancelOrder(Long id) {
        shopService.cancelOrder(id);
        Optional<Purchase> purchaseOptional = purchaseService.findById(id);
        assertTrue(purchaseOptional.isPresent());
        assertEquals(OrderStatus.CANCELED, purchaseOptional.get().getOrderStatus());
    }

    private void shopBudgetTest(Long shopID, BigDecimal oldBudget, BigDecimal purchaseSumPrice) {
        Optional<Shop> shopOptional = shopService.findById(shopID);
        assertTrue(shopOptional.isPresent());
        Shop shop = shopOptional.get();
        BigDecimal newBudget = shop.getBudget();
        assertEquals(newBudget, oldBudget.add(purchaseSumPrice));
    }

    private int getFreeSpaceInMainStorage(Long shopID) {
        Optional<ShopStorage> shopStorageOptional
                = shopStorageRepository.findByShopIdAndType(shopID, 1);
        assertTrue(shopStorageOptional.isPresent());
        return shopStorageOptional.get().getFreeSpace();
    }

    @Test
    public void addBudgetTest() {
        logger.info("Добавление денег в бюджет магазина");
        Long shopID = 1L;
        Optional<Shop> shopOptional = shopService.findById(shopID);
        assertTrue(shopOptional.isPresent());
        Shop shop = shopOptional.get();
        BigDecimal oldBudget = shop.getBudget();
        String money1 = "1000.9990";
        shopService.addBudget(1L, money1);

        shopOptional = shopService.findById(shopID);
        assertTrue(shopOptional.isPresent());
        shop = shopOptional.get();
        BigDecimal newBudget = shop.getBudget();
        assertEquals(oldBudget.add(new BigDecimal(money1)), newBudget);
    }

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
