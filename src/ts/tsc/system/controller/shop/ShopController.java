package ts.tsc.system.controller.shop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.controller.response.BaseResponseBuilder;
import ts.tsc.system.controller.parent.ExtendedControllerInterface;
import ts.tsc.system.controller.parent.OrderController;
import ts.tsc.system.controller.parent.ShopOrderInterface;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.controller.status.OrderStatus;
import ts.tsc.system.entity.parent.BaseStorage;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.entity.purchase.Purchase;
import ts.tsc.system.entity.purchase.PurchaseProduct;
import ts.tsc.system.entity.purchase.PurchaseProductPrimaryKey;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.entity.shop.ShopStorage;
import ts.tsc.system.entity.shop.ShopStorageProduct;
import ts.tsc.system.entity.shop.ShopStorageProductPrimaryKey;
import ts.tsc.system.repository.named.NamedRepository;
import ts.tsc.system.service.base.BaseService;
import ts.tsc.system.service.named.NamedService;
import ts.tsc.system.service.order.OrderInterface;
import ts.tsc.system.service.order.ShopInterface;
import ts.tsc.system.service.storage.manager.StorageServiceInterface;
import ts.tsc.system.service.storage.manager.StorageServiceManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/shop")
public class ShopController extends OrderController
        implements
        ShopOrderInterface,
        ExtendedControllerInterface<Shop, ShopStorage> {

    private final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @PersistenceContext
    EntityManager entityManager;

    private final ShopInterface shopService;
    private final StorageServiceInterface<Shop, ShopStorage, Long> storageServiceInterface;
    private final BaseService<ShopStorage, Long> shopStorageService;
    private final BaseService<Purchase, Long> purchaseService;

    private final BaseResponseBuilder<Shop> shopBaseResponseBuilder;
    private final BaseResponseBuilder<ShopStorage> shopStorageBaseResponseBuilder;
    private final BaseResponseBuilder<Purchase> purchaseBaseResponseBuilder;

    @Autowired
    public ShopController(@Qualifier(value = "shopService") ShopInterface shopService,
                          @Qualifier(value = "shopStorageService") StorageServiceInterface<Shop, ShopStorage, Long> storageServiceInterface,
                          BaseService<ShopStorage, Long> shopStorageService,
                          @Qualifier(value = "purchaseService") BaseService<Purchase, Long> purchaseService,
                          BaseResponseBuilder<Shop> shopBaseResponseBuilder,
                          BaseResponseBuilder<ShopStorage> shopStorageBaseResponseBuilder,
                          BaseResponseBuilder<Purchase> purchaseBaseResponseBuilder) {
        this.shopService = shopService;
        this.storageServiceInterface = storageServiceInterface;
        this.shopStorageService = shopStorageService;
        this.purchaseService = purchaseService;
        this.shopBaseResponseBuilder = shopBaseResponseBuilder;
        this.shopStorageBaseResponseBuilder = shopStorageBaseResponseBuilder;
        this.purchaseBaseResponseBuilder = purchaseBaseResponseBuilder;
    }

    /**
     * Поиск всех магазинов
     * @return {@link ts.tsc.system.service.base.BaseServiceImplementation#findAll(JpaRepository)}
     */
    @Override
    @GetMapping(value = "/list")
    public ResponseEntity<?> findAll() {
        return shopBaseResponseBuilder.getAll(shopService.findAll());
    }

    /**
     * Поиск по названию магазина
     * @param name название, по которому будет происходить поиск
     * @return {@link ts.tsc.system.service.named.NamedServiceImplementation#findByName(String, NamedRepository)}
     */
    @Override
    @GetMapping(value = "/name/{name}")
    public ResponseEntity<?>  findByName(@PathVariable String name) {
        return shopStorageBaseResponseBuilder.getAll(shopService.findByName(name));
    }

    /**
     * Поиск магазина по идентификатору
     * @param id идентификатор запрашиваемого объекта
     * @return {@link ts.tsc.system.service.base.BaseServiceImplementation#findById(Object, JpaRepository)}
     */
    @Override
    @GetMapping(value = "/{id}")
    public ResponseEntity<?>  findById(@PathVariable Long id) {
        Optional<Shop> shopOptional = shopService.findById(id);
        return shopOptional.<ResponseEntity<?>>map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    /**
     * Добавление нового магазина
     * @param shop объект типа Shop
     * @return 1) код 400 с сообщением ID_CAN_NOT_BE_SET_IN_JSON, если в теле json задан идентификатор
     *         2) {@link ts.tsc.system.service.base.BaseServiceImplementation#save(Object, JpaRepository)}
     */
    @Override
    @PostMapping(value = "/")
    public ResponseEntity<?> create(@RequestBody Shop shop) {
        if(shop.getId() !=null) {
            return new ResponseEntity<>(ErrorStatus.ID_CAN_NOT_BE_SET_IN_JSON, HttpStatus.BAD_REQUEST);
        }
        return shopBaseResponseBuilder.save(shopService.save(shop));
    }

    /**
     * Обновление данных магазина
     * @param id идентификатор искомого магазина
     * @param shop объект
     * @return 1) объект и код 200, если удалось обновить,
     *         2) код 422, если не удалось найти
     *         3) код 400 с сообщением ID_CAN_NOT_BE_SET_IN_JSON, если в теле json задан идентификатор
     */
    @Override
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Shop shop) {
        if(shop.getId() !=null) {
            return new ResponseEntity<>(ErrorStatus.ID_CAN_NOT_BE_SET_IN_JSON, HttpStatus.BAD_REQUEST);
        }
        Optional<Shop> baseShopOptional = shopService.findById(id);
        if(baseShopOptional.isPresent()) {
            return shopBaseResponseBuilder.save(shopService.update(id, shop));
        } else {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":shop", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Поиск склада по идентификтаору
     * @param id идентификатор склада
     * @return {@link StorageServiceManager#findById(Object, JpaRepository)}
     */
    @Override
    @GetMapping(value = "/storage/{id}")
    public ResponseEntity<?> findStorageById(@PathVariable Long id) {
        String stringQuery = "select entity from ShopStorage entity where entity.id = ?1";
        return storageServiceInterface.findById(id, stringQuery);
    }


    /**
     * Поиск всех складов магазинов
     * @return {@link ts.tsc.system.service.base.BaseServiceImplementation#findAll(JpaRepository)}
     */
    @Override
    @GetMapping(value = "/storage/list")
    public ResponseEntity<?>  findAllStorage() {
        return shopStorageBaseResponseBuilder.getAll(storageServiceInterface.findAll());
    }

    /**
     * Поиск складов по идентификтору магазина
     * @param id идентификтаор магазина
     * @return {@link StorageServiceManager#findById(Long, String, JpaRepository)}
     */
    @Override
    @GetMapping(value = "/storage/list/{id}")
    public ResponseEntity<?> findStorageByOwnerId(@PathVariable Long id) {
        String stringQuery = "select entity from ShopStorage entity where entity.shop.id = ?1";
        return storageServiceInterface.findById(id, stringQuery);
    }

    /**
     * Поиск заказа по идентификатору
     * @param id идентификатор заказа
     * @return {@link ts.tsc.system.service.base.BaseServiceImplementation#findById(Object, JpaRepository)}
     */
    @Override
    @GetMapping(value = "/order/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Optional<Purchase> purchaseOptional = purchaseService.findById(id);
        return purchaseOptional.<ResponseEntity<?>>map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    /**
     * Добавление склада магазину
     * @param id идентификатор магазина
     * @param storage объект типа Storage, которы йбудет добавлен
     * @return  1) код 400 с сообщением ID_CAN_NOT_BE_SET_IN_JSON, если в теле json задан идентификатор
     *          2) {@link StorageServiceManager#addStorage(Object, BaseStorage, JpaRepository)}
     */
    @Override
    @PostMapping(value = "/storage/{id}")
    public ResponseEntity<?> addStorage(@PathVariable Long id, @RequestBody ShopStorage storage) {
        if(storage.getId() !=null) {
            return new ResponseEntity<>(ErrorStatus.ID_CAN_NOT_BE_SET_IN_JSON, HttpStatus.BAD_REQUEST);
        }
        return storageServiceInterface.addStorage(id, storage, shopService);
    }


    /**
     * Получение списка продуктов со склада
     * @return {@link OrderController#getStorageProducts(Long, JpaRepository)}
     */
    @Override
    @SuppressWarnings("unchecked")
    @GetMapping(value = "/storage/product/list/{id}")
    public ResponseEntity<?> getStorageProducts(@PathVariable Long id) {
        return getStorageProducts(id, shopStorageService);
    }

    /**
     * Получение списка заказов
     * @return {@link ts.tsc.system.service.base.BaseServiceImplementation#findAll(JpaRepository)}
     */
    @Override
    @GetMapping(value = "/order/list")
    public ResponseEntity<?> getAllOrders() {
        return purchaseBaseResponseBuilder.getAll(purchaseService.findAll());
    }

    /**
     * Обработка поступившего заказа
     * @param shopID идентификатор магазина, в котором запрошена покупка
     * @param productIdList списко идентификтаоров продуктов
     * @param countList список количества продуктов
     * @return 1) Если количество элементов списке идентификаторов товаров и их количества разное
     *            возвращается код 400 с сообщением WRONG_NUMBER_OF_PARAMETERS
     *         2) код 404 с сообщением ELEMENT_NOT_FOUND:shop - если не найден магазин с указанным id
     *         3) код 404 с сообщением ELEMENT_NOT_FOUND:internal_storage - если не найден внутренний склад у магазина
     *         4) код 404 с сообщением ELEMENT_NOT_FOUND:product - если не удалось найти какой-то товар на складе
     *         5) код 400 с оообщением NOT_ENOUGH_PRODUCTS - если на складе не хватает какого-либо товара
     *         6) код 500 с сообщением ERROR_WHILE_SAVING - если не удалось сохранить заказ
     *         7) {@link #transfer(List, List, ShopStorage, Purchase, Shop)}
     */
    @Override
    @PostMapping (value = "/order/{shopID}/{productIdList}/{countList}")
    public ResponseEntity<?> receiveOrder(@PathVariable Long shopID,
                                          @PathVariable List<Long> productIdList,
                                          @PathVariable List<Integer> countList) {
        return shopService.receiveOrder(shopID, productIdList, countList);
    }


    /**
     * Изменения состояния заказа
     * @param id идентификтаор заказа
     * @param status состояние
     * @return {@link OrderController#changeStatus(Long, String)}
     */
    @Override
    @PutMapping(value = "/order/status/{id}/{status}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @PathVariable String status) {
        return super.changeStatus(id, status);
    }



    @Override
    protected OrderInterface getService() {
        return shopService;
    }

    /**
     * Перевод товаров с одного склада в другой
     * @param shopStorageID идентификатор склада, с которого будет производиться перевоз товаров
     * @param targetShopStorageID идентификтаор целевого склада
     * @param productIDList список идентификтаоров товаров
     * @param countList список количества товаров
     * @return 1) код 400 c сообщением WRONG_NUMBER_OF_PARAMETERS, если количество элементов в списках разное
     *         2) код 404 с сообщением ELEMENT_NOT_FOUND:source_storage, если не найден исходный склад
     *         3) код 404 с сообщением ELEMENT_NOT_FOUND:target_storage, если не найден целевой склад
     *         4) код 400 с сообщением NOT_ENOUGH_SPACE:target_storage - если не хватает места на складе магазина
     *         5) код 500 с сообщением BAD_QUERY, если не удалось выполнить запрос
     *         6) код 400 с сообщением NOT_ENOUGH_PRODUCTS - если на складе не хватает какого-нибудь товара
     *         7) код 200 с объектом, если удалось выполнить запрос
     */
    @PutMapping(value = "/storage/{shopStorageID}/{targetShopStorageID}/{productIDList}/{countList}")
    public ResponseEntity<?> transferProducts(@PathVariable Long shopStorageID,
                                                      @PathVariable Long targetShopStorageID,
                                                      @PathVariable List<Long> productIDList,
                                                      @PathVariable List<Integer> countList) {
        return shopService.transferProducts(shopStorageID, targetShopStorageID, productIDList, countList);
    }


    @PutMapping(value = "/budget/{id}/{budgetString:.+}")
    public ResponseEntity<?> addBudget(@PathVariable Long id, @PathVariable String budgetString) {
        return shopService.addBudget(id, budgetString);
    }

}

