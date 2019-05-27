package test.shop;

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

/**
 * Тестирование заказов в магазине
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {TestDataServiceConfig.class})
@WebAppConfiguration
public class ShopOrderTest{

    private final static Logger logger = LoggerFactory.getLogger(ShopOrderTest.class);

    @Autowired
    public ShopInterface shopService;
    @Autowired
    public BaseServiceInterface<Purchase, Long> purchaseService;
    @Autowired
    public ShopStorageRepository shopStorageRepository;

    /**
     * Тестирование процесса поступления заказа, доставки и завершения
     */
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

    /**
     * Тестирование процесса поступления заказа и его отмены
     */
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

    /**
     * Тестирование процесса поступления заказа, доставки и отмены
     */
    @Test
    public void receiveAndDeliverAndCancelOrderTest() {
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

    /**
     * Тестирование операций при поступлении заказа
     * @param shopID идентификатор магазина
     * @param productIDList список идентификаторов товаров
     * @param countList список поличества товаров
     * @return массив переменных, которые могут понадобиться на других этапах заказа:
     *          1) идентификатор заказа
     *          2) бюджет магазина до заказа
     *          3) сумма заказа
     *          4) свободное место на складе до заказа
     */
    private Object[] receiveOrder(Long shopID, List<Long> productIDList, List<Integer> countList) {
        Optional<Shop> shopOptional = shopService.findById(shopID);
        assertTrue(shopOptional.isPresent());
        Shop shop = shopOptional.get();
        BigDecimal oldBudget = shop.getBudget();
        int oldFreeSpace = getFreeSpaceInMainStorage(shopID);

        //Проверка результата заказа
        ResponseEntity<?> responseEntity = shopService
                .receiveOrder(shopID, Arrays.asList(1L,2L), Arrays.asList(10,10));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Purchase purchase = (Purchase) responseEntity.getBody();
        assertNotNull(purchase);
        assertEquals(OrderStatus.RECEIVED, purchase.getOrderStatus());
        Optional<Purchase> purchaseOptional = purchaseService.findById(purchase.getId());
        assertTrue(purchaseOptional.isPresent());
        purchase = purchaseOptional.get();
        assertNotNull(purchase.getPurchaseProducts());
        Set<PurchaseProduct> purchaseProductSet = purchase.getPurchaseProducts();
        assertNotNull(purchaseProductSet);

        List<PurchaseProduct> purchaseProductList = purchaseProductSet.stream()
                .sorted(Comparator.comparing(item->item.getPrimaryKey().getProduct().getId()))
                .collect(Collectors.toList());

        //Проверка в заказе указанных данных
        for (int iterator =0; iterator <  purchaseProductList.size(); iterator++) {
            assertEquals(purchaseProductList.get(iterator).getCount(),
                    (int) countList.get(iterator));
            assertEquals(purchaseProductList.get(iterator).getPrimaryKey().getProduct().getId(),
                    productIDList.get(iterator));
        }

        BigDecimal purchaseSumPrice = new BigDecimal(0);
        int purchaseProductCount = 0;
        for(PurchaseProduct purchaseProduct : purchaseProductList) {
            purchaseProductCount +=purchaseProduct.getCount();
            purchaseSumPrice = purchaseSumPrice.add(purchaseProduct.getSumPrice());
        }

        //Проверка свободного места на складе магазина
        Optional<Shop> updatedShop = shopService.findById(shopID);
        assertTrue(updatedShop.isPresent());
        int newFreeSpace = getFreeSpaceInMainStorage(shopID);
        assertEquals(newFreeSpace, oldFreeSpace+purchaseProductCount);

        return new Object[]{purchase.getId(), oldBudget, purchaseSumPrice, oldFreeSpace};
    }

    /**
     * Тестрование перевода заказа в статус DELIVERING
     * @param id идентификатор заказа
     */
    public void deliverOrder(Long id) {
        shopService.deliverOrder(id);
        Optional<Purchase> purchaseOptional = purchaseService.findById(id);
        assertTrue(purchaseOptional.isPresent());
        assertEquals(OrderStatus.DELIVERING, purchaseOptional.get().getOrderStatus());
    }

    /**
     * Тестирование завершения заказа
     * @param id идентификатор заказа
     */
    private void completeOrder(Long id) {
        shopService.completeOrder(id);
        Optional<Purchase> purchaseOptional = purchaseService.findById(id);
        assertTrue(purchaseOptional.isPresent());
        assertEquals(OrderStatus.COMPLETED, purchaseOptional.get().getOrderStatus());
    }

    /**
     * Тестирование отмены заказа
     * @param id идентификатор заказа
     */
    private void cancelOrder(Long id) {
        shopService.cancelOrder(id);
        Optional<Purchase> purchaseOptional = purchaseService.findById(id);
        assertTrue(purchaseOptional.isPresent());
        assertEquals(OrderStatus.CANCELED, purchaseOptional.get().getOrderStatus());
    }

    /**
     * Проверка бюджета в магазине между разными операциями
     * @param shopID идентификатор магазина
     * @param oldBudget старый показатель бюджета
     * @param purchaseSumPrice сумма заказа
     */
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
}
