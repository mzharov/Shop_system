package test.supplier;

import org.junit.AfterClass;
import org.junit.BeforeClass;
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
import test.config.TestDataServiceConfig;
import ts.tsc.system.controller.status.OrderStatus;
import ts.tsc.system.entity.delivery.Delivery;
import ts.tsc.system.entity.delivery.DeliveryProduct;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.entity.shop.ShopStorage;
import ts.tsc.system.entity.shop.ShopStorageProduct;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.entity.supplier.SupplierStorage;
import ts.tsc.system.entity.supplier.SupplierStorageProduct;
import ts.tsc.system.service.base.BaseServiceInterface;
import ts.tsc.system.service.shop.ShopInterface;
import ts.tsc.system.service.storage.manager.StorageServiceInterface;
import ts.tsc.system.service.supplier.SupplierInterface;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


/**
 * Абстрактный класс, содежащий методы для тестирование
 * процесса заказа у поставщика
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {TestDataServiceConfig.class})
@WebAppConfiguration
public abstract class SupplierOrderTest {


    @Autowired
    SupplierInterface supplierService;
    @Autowired
    BaseServiceInterface<Delivery, Long> deliveryService;
    @Autowired
    StorageServiceInterface<Supplier, SupplierStorage, Long> supplierStorageService;
    @Autowired
    StorageServiceInterface<Shop, ShopStorage, Long> shopStorageService;
    @Autowired
    ShopInterface shopService;

    private final static Logger logger = LoggerFactory.getLogger(SupplierOrderTest.class);

    @BeforeClass
    public static void setUp() {
        logger.info("Начало теста SupplierService");
    }
    @AfterClass
    public static void tearDown() {
        logger.info("Конец теста SupplierService");
    }

    /**
     * Метод, в котором проверяется операция поступления заказа
     * @param supplierStorageID идентификатор склада поставщика
     * @param shopStorageID идентификатор склада магазина
     * @param productIDList список идентификтаоров товаров
     * @param countList список количества товаров
     * @return массив параметров, которые могут понадобиться на следующих этапах тестирования:
     *          1) идентификатор заказа
     *          2) количество свободного места на складе поставщика до заказа
     *          3) количество свободного места на складе магазина до заказа
     *          4) количество заказанных товаров
     *          5) общая сумма заказа
     *          6) бюджет магазина до заказа
     *          7) список количества товаров на складе поставщика до заказа
     *          8) список количества товаров на складе магазина до заказа
     *
     */
    Object[] receiveOrder(Long supplierStorageID,
                                  Long shopStorageID,
                                  List<Long> productIDList,
                                  List<Integer> countList) {

        Optional<SupplierStorage> supplierStorageOptional
                = supplierStorageService.findById(supplierStorageID);
        assertTrue(supplierStorageOptional.isPresent());
        SupplierStorage supplierStorage = supplierStorageOptional.get();
        Integer supplierPreviousFreeSpace = supplierStorage.getFreeSpace();
        Optional<ShopStorage> shopStorageOptional = shopStorageService.findById(shopStorageID);
        assertTrue(shopStorageOptional.isPresent());
        ShopStorage shopStorage = shopStorageOptional.get();
        assertNotNull(shopStorage);
        Shop shop = shopStorage.getShop();
        int shopPreviousFreeSpace = shopStorage.getFreeSpace();

        List<Integer> supplierPreviousCount  = supplierStorage
                .getProducts().stream()
                .map(SupplierStorageProduct::getCount)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        List<Integer> shopPreviousCount = shopStorage
                .getProducts().stream()
                .map(ShopStorageProduct::getCount)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        BigDecimal shopPreviousBudget = shop.getBudget();
        ResponseEntity<?> responseEntity
                = supplierService.receiveOrder(supplierStorageID, shopStorageID, productIDList, countList);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Delivery delivery = (Delivery) responseEntity.getBody();
        assertNotNull(delivery);
        assertEquals(delivery.getOrderStatus(), OrderStatus.RECEIVED);
        Optional<Delivery> deliveryOptional =  deliveryService.findById(delivery.getId());
        assertTrue(deliveryOptional.isPresent());
        delivery = deliveryOptional.get();

        Set<DeliveryProduct> deliveryProductSet = delivery.getDeliveryProducts();
        assertNotNull(deliveryProductSet);

        List<DeliveryProduct> deliveryProductList = deliveryProductSet.stream()
                .sorted(Comparator.comparing(item->item.getPrimaryKey().getProduct().getId()))
                .collect(Collectors.toList());
        assertEquals(2, deliveryProductList.size());

        BigDecimal productPriceSum = new BigDecimal(0);

        for (int iterator = 0; iterator <  deliveryProductList.size(); iterator++) {
            assertEquals(deliveryProductList.get(iterator).getCount(),
                    (int) countList.get(iterator));
            assertEquals(deliveryProductList.get(iterator).getPrimaryKey().getProduct().getId(),
                    productIDList.get(iterator));
            productPriceSum = productPriceSum.add(deliveryProductList.get(iterator).getSumPrice());
        }
        supplierStorageOptional
                = supplierStorageService.findById(supplierStorageID);
        assertTrue(supplierStorageOptional.isPresent());
        supplierStorage = supplierStorageOptional.get();
        int supplierNewFreeSpace = supplierStorage.getFreeSpace();
        Integer productCountSum = countList.stream().reduce(0, Integer::sum);
        assertEquals(supplierNewFreeSpace, productCountSum+supplierPreviousFreeSpace);

        return new Object[]{
                delivery.getId(),
                supplierPreviousFreeSpace,
                shopPreviousFreeSpace,
                productCountSum,
                productPriceSum,
                shopPreviousBudget,
                supplierPreviousCount,
                shopPreviousCount};
    }

    /**
     * Тестирование перевода заказа в статус DELIVERING
     * @param id идентификатор заказа
     */
    void deliverOrder(Long id) {
        supplierService.deliverOrder(id);
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);
        assertTrue(deliveryOptional.isPresent());
        Delivery delivery = deliveryOptional.get();
        assertEquals(OrderStatus.DELIVERING, delivery.getOrderStatus());
    }

    /**
     * Тестирование завершения заказа
     * @param deliveryID идентификатор заказа
     * @param shopStorageID идентификатор склада магазина
     * @param productCountSum сумма количества всех заказанных товаров
     * @param productPriceSum сумма цен на заказанные товары
     * @param shopPreviousBudget бюджет магазина до заказа
     */
    void completeOrder(Long deliveryID, Long shopStorageID,
                               int productCountSum,
                               BigDecimal productPriceSum,
                               BigDecimal shopPreviousBudget) {
        Optional<ShopStorage> shopStorageOptional = shopStorageService.findById(shopStorageID);
        assertTrue(shopStorageOptional.isPresent());
        ShopStorage shopStorage = shopStorageOptional.get();

        int shopPreviousFreeSpace = shopStorage.getFreeSpace();

        supplierService.completeOrder(deliveryID);

        //Проверка статуса
        Optional<Delivery> deliveryOptional = deliveryService.findById(deliveryID);
        assertTrue(deliveryOptional.isPresent());
        Delivery delivery = deliveryOptional.get();
        assertEquals(OrderStatus.COMPLETED, delivery.getOrderStatus());

        //Сравнение ожидаемой величины свободного места на складе с реальной
        shopStorageOptional = shopStorageService.findById(shopStorageID);
        assertTrue(shopStorageOptional.isPresent());
        shopStorage = shopStorageOptional.get();
        int newFreeSpace = shopStorage.getFreeSpace();
        assertEquals(newFreeSpace, shopPreviousFreeSpace-productCountSum);

        //Сравнение ожидаемой величины бюджета магазина с реальной
        Optional<Shop> shopOptional = shopService.findById(shopStorage.getShop().getId());
        assertTrue(shopOptional.isPresent());
        Shop shop = shopOptional.get();
        BigDecimal newShopBudget = shop.getBudget();
        assertEquals(newShopBudget, shopPreviousBudget.subtract(productPriceSum));

    }

    /**
     * Тестирование операции отмены заказа
     * @param id идентификатор заказа
     * @param supplierPreviousFreeSpace количество свободного места на складе поставщика
     * @param shopPreviousFreeSpace количество свободного места на складе магазина
     * @param shopPreviousBudget бюджет магазина до заказа
     * @param supplierPreviousProductCount список количества товаров на складе поставщика до заказа
     * @param shopPreviousProductCount список количества товаров на складе магазина до заказа
     */
    void cancelOrder(Long id,
                     int supplierPreviousFreeSpace,
                     int shopPreviousFreeSpace,
                     BigDecimal shopPreviousBudget,
                     List<Integer> supplierPreviousProductCount,
                     List<Integer> shopPreviousProductCount) {
        ResponseEntity<?> responseEntity = supplierService.cancelOrder(id);
        assertNotNull(responseEntity);

        //Проверка статуса
        Delivery delivery = (Delivery) responseEntity.getBody();
        assertNotNull(delivery);
        assertEquals(OrderStatus.CANCELED, delivery.getOrderStatus());

        //Сравнение ожидаемой величины свободного места на складе поставщика с реальной
        SupplierStorage supplierStorage = delivery.getSupplierStorage();
        assertNotNull(supplierStorage);
        assertEquals(supplierPreviousFreeSpace, supplierStorage.getFreeSpace());

        /*
         * Сравнение ожидаемой величины бюджета и количества свободного местана складе магазина
         * с реальными величинами
         */
        ShopStorage shopStorage = delivery.getShopStorage();
        assertNotNull(shopStorage);
        Shop shop = shopStorage.getShop();
        assertNotNull(shop);
        assertEquals(shopPreviousBudget, shop.getBudget());
        assertEquals(shopPreviousFreeSpace, shopStorage.getFreeSpace());

        //Сравнение ожидаемых величин количества товаров с реальными
        List<Integer> shopNewCount = shopStorage
                .getProducts().stream()
                .map(ShopStorageProduct::getCount)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
        assertEquals(shopPreviousProductCount.size(), shopNewCount.size());

        for (int iterator = 0; iterator < shopStorage.getProducts().size(); iterator++) {
            assertEquals(shopPreviousProductCount.get(iterator),
                    shopNewCount.get(iterator));
        }

        //Сравнение ожидаемых величин количества товаров на складе поставщика с реальными
        List<Integer> supplierNewCount = supplierStorage
                .getProducts().stream()
                .map(SupplierStorageProduct::getCount)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
        assertEquals(supplierPreviousProductCount.size(), supplierNewCount.size());

        for (int iterator = 0; iterator < supplierNewCount.size(); iterator++) {
            assertEquals(supplierPreviousProductCount.get(iterator),
                    supplierNewCount.get(iterator));
        }

    }
}
