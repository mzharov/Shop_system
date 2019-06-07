package ts.tsc.system.controller.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.controller.aspect.IDValidation;
import ts.tsc.system.controller.parent.ExtendedControllerInterface;
import ts.tsc.system.controller.parent.OrderController;
import ts.tsc.system.controller.response.BaseResponseBuilder;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.entity.delivery.Delivery;
import ts.tsc.system.entity.parent.BaseStorage;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.entity.supplier.SupplierStorage;
import ts.tsc.system.service.base.BaseService;
import ts.tsc.system.service.base.BaseServiceInterface;
import ts.tsc.system.service.named.NamedService;
import ts.tsc.system.service.named.NamedServiceInterface;
import ts.tsc.system.service.order.OrderInterface;
import ts.tsc.system.service.supplier.SupplierInterface;
import ts.tsc.system.service.storage.manager.StorageServiceInterface;
import ts.tsc.system.service.storage.manager.StorageServiceManager;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/app/supplier")
public class SupplierController
        extends OrderController
        implements ExtendedControllerInterface<Supplier, SupplierStorage> {

    private final SupplierInterface supplierService;
    private final BaseServiceInterface<Delivery, Long> deliveryService;
    private final StorageServiceInterface<Supplier, SupplierStorage, Long> supplierStorageService;

    private final BaseResponseBuilder<Supplier> supplierBaseResponseBuilder;
    private final BaseResponseBuilder<Delivery> deliveryBaseResponseBuilder;
    private final BaseResponseBuilder<SupplierStorage> supplierStorageBaseResponseBuilder;

    @Autowired
    public SupplierController(BaseServiceInterface<Delivery, Long> deliveryService,
                              StorageServiceInterface<Supplier, SupplierStorage, Long> supplierStorageService,
                              SupplierInterface supplierService,
                              BaseResponseBuilder<Supplier> supplierBaseResponseBuilder,
                              BaseResponseBuilder<Delivery> deliveryBaseResponseBuilder,
                              BaseResponseBuilder<SupplierStorage> supplierStorageBaseResponseBuilder) {
        this.deliveryService = deliveryService;
        this.supplierStorageService = supplierStorageService;
        this.supplierService = supplierService;
        this.supplierBaseResponseBuilder = supplierBaseResponseBuilder;
        this.deliveryBaseResponseBuilder = deliveryBaseResponseBuilder;
        this.supplierStorageBaseResponseBuilder = supplierStorageBaseResponseBuilder;
    }

    /**
     * Поиск всех поставщиков
     * @return {@link BaseService#findAll()}
     */
    @Override
    @GetMapping(value = "/list")
    public ResponseEntity<?> findAll() {
        return supplierBaseResponseBuilder.getAll(supplierService.findAll());
    }

    /**
     * Поиск по названию магазина
     * @param name название, по которому будет происходить поиск
     * @return {@link NamedService#findByName(String)}
     */
    @Override
    @GetMapping(value = "/name/{name}")
    public ResponseEntity<?> findByName(@PathVariable String name) {
        return supplierStorageBaseResponseBuilder.getAll(supplierService.findByName(name));
    }

    /**
     * Поиск магазина по идентификатору
     * @param id идентификатор запрашиваемого объекта
     * @return {@link BaseService#findById(Object)}
     */
    @Override
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<Supplier> supplierOptional = supplierService.findById(id);
        return supplierOptional.<ResponseEntity<?>>map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    /**
     * Добавление нового магазина
     * @param supplier объект типа Supplier
     * @return 1) код 400 с сообщением ID_CAN_NOT_BE_SET_IN_JSON
     *         2) {@link BaseService#save(Object)}
     */
    @Override
    @PostMapping(value = "/")
    @IDValidation
    public ResponseEntity<?> create(@RequestBody Supplier supplier) {
        return supplierBaseResponseBuilder.save(supplierService.save(supplier));
    }

    /**
     * Обновление данных магазина
     * @param id идентификатор искомого магазина
     * @param supplier объект
     * @return 1) объект и код 200, если удалось обновить,
     *         2) иначе код 422
     *         3) код 400 с сообщением ID_CAN_NOT_BE_SET_IN_JSON, если в теле json задан другой идентификатор
     */
    @Override
    @PutMapping(value = "/{id}")
    @IDValidation
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Supplier supplier) {
        Optional<Supplier> supplierOptional = supplierService.findById(id);
        if(supplierOptional.isPresent()) {
            return supplierBaseResponseBuilder.save(supplierService.update(id, supplier));
        } else {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":supplier", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Поиск склада по идентификтаору
     * @param id идентификтаор склада
     * @return {@link StorageServiceManager#findById(Object)}
     */
    @Override
    @GetMapping(value = "/storage/{id}")
    public ResponseEntity<?> findStorageById(@PathVariable Long id) {
        String stringQuery = "select entity from SupplierStorage entity where entity.id = ?1";
        return supplierStorageService.findById(id, stringQuery);
    }

    /**
     * Поиск всех складов магазинов
     * @return {@link BaseService#findAll()}
     */
    @Override
    @GetMapping(value = "/storage/list")
    public ResponseEntity<?> findAllStorage() {
        return supplierStorageBaseResponseBuilder.getAll(supplierStorageService.findAll());
    }

    /**
     * Поиск складов по идентификтору магазина
     * @param id идентификтаор магазина
     * @return {@link StorageServiceManager#findById(Long, String)}
     */
    @Override
    @GetMapping(value = "/storage/list/{id}")
    public ResponseEntity<?> findStorageByOwnerId(@PathVariable Long id) {
        String stringQuery = "select entity from SupplierStorage entity where entity.supplier.id = ?1";
        return supplierStorageService.findById(id, stringQuery);
    }

    /**
     * Поиск заказа по идентификатору
     * @param id идентификатор заказа
     * @return {@link BaseService#findById(Object)}
     */
    @Override
    @GetMapping(value = "/order/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);
        return deliveryOptional.<ResponseEntity<?>>map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    /**
     * Добавление склада магазину
     * @param id идентификатор магазина
     * @param storage объект типа Storage, который будет добавлен
     * @return  1) код 400 с сообщением ID_CAN_NOT_BE_SET_IN_JSON, если в теле json задан идентификатор
     *          2) {@link StorageServiceManager#addStorage(Object, BaseStorage, NamedServiceInterface)} )}
     */
    @Override
    @PostMapping(value = "/storage/{id}")
    @IDValidation
    public ResponseEntity<?> addStorage(@PathVariable Long id, @RequestBody SupplierStorage storage) {
        return supplierStorageService.addStorage(id, storage, supplierService);
    }


    /**
     * Получение списка продуктов со склада
     * @return {@link OrderController#getStorageProducts(Long, BaseServiceInterface)}
     */
    @Override
    @SuppressWarnings("unchecked")
    @GetMapping(value = "/storage/product/list/{id}")
    public ResponseEntity<?> getStorageProducts(@PathVariable Long id) {
        return getStorageProducts(id, supplierStorageService);
    }

    /**
     * Обработка поступившего заказа
     * @param supplierID идентификатор склада поставщика
     * @param shopStorageID идентификатор склада магазина
     * @param productIdList список идентификаторов товаров
     * @param countList список количества товаров
     * @return 1) Если количество элементов списке идентификаторов товаров и их количества разное
     *            возвращается код 400 с сообщением WRONG_NUMBER_OF_PARAMETERS
     *         2) код 404 с сообщением ELEMENT_NOT_FOUND:supplier_storage - если не найден склад поставщика с указанным id
     *         3) код 404 с сообщением ELEMENT_NOT_FOUND:shop_storage - если не найден склад магазина с указанным id
     *         4) код 400 с сообщением NOT_ENOUGH_SPACE - если не хватает места на складе магазина для товаров
     *         5) код 400 с сообщением NOT_ENOUGH_PRODUCTS - если на складе не хватает какого-нибудь товара
     *         6) код 404 с сообщением ELEMENT_NOT_FOUND:product - если не удалось найти какой-то товар на складе
     *         7) код 404 с сообщением ELEMENT_NOT_FOUND:shop, если не удалось найти магазин
     *         8) код 400 с сообщением NOT_ENOUGH_MONEY, если у магазина не хватает бюджета
     *         9) код 500 с сообщением ERROR_WHILE_SAVING, если не удалось сохранить заказ
     *         10) {@see ts.tsc.system.service.supplier.SupplierService#transfer(List, List, SupplierStorage, Delivery, Shop)}
     *
     */
    @PostMapping(value = "/order/{supplierID}/{shopStorageID}/{productIdList}/{countList}")
    public ResponseEntity<?> receiveOrder(@PathVariable Long supplierID,
                             @PathVariable Long shopStorageID,
                             @PathVariable List<Long> productIdList,
                             @PathVariable List<Integer> countList) {
        return supplierService.receiveOrder(supplierID, shopStorageID, productIdList, countList);
    }


    /**
     * Изменение состояние заказа
     * @param id идентификатор заказа
     * @param status новое состояние
     * @return {@link OrderController#changeStatus(Long, String)}
     */
    @PutMapping(value = "/order/status/{id}/{status}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @PathVariable String status) {
        return super.changeStatus(id, status);
    }

    /**
     * Поиск всех доставок
     * @return {@link BaseServiceInterface#findAll()}
     */
    @GetMapping(value = "/order/list")
    public ResponseEntity<?> getAllOrders() {
        return deliveryBaseResponseBuilder.getAll(deliveryService.findAll());
    }

    @Override
    protected OrderInterface getService() {
        return supplierService;
    }

    /**
     * Добавление товаров на склад
     * @param id идентификатор склада
     * @param productIDList список идентификтаоров товаров
     * @param countList список количества товаров
     * @return 1) код 400 c сообщением WRONG_NUMBER_OF_PARAMETERS, если количество элементов в списках разное
     *         2) код 400 с сообщением NUMBER_FORMAT_EXCEPTION, если не удалось преобразовать строкове значение в BigDecimal
     *         3) код 404 с сообщением ELEMENT_NOT_FOUND:supplier_storage, если не найден склад
     *         4) код 400 с сообщением NOT_ENOUGH_SPACE - если не хватает места на складе
     *         5) код 404 с сообщением ELEMENT_NOT_FOUND:product, если не найден товар
     *         6) код 500 с сообщением ERROR_WHILE_SAVING:supplier_storage_product, если не удалось сохранить изменения
     *         7) код 500 с сообщением ERROR_WHILE_SAVING:supplier_storage, если не удалось сохранить изменения
     *         8) код 200 с объектом, если удалось выполнить запрос
     */
    @PutMapping(value = "/storage/{id}/{productIDList}/{countList}/{stringPriceList:.+}")
    ResponseEntity<?> addProductsToStorage(@PathVariable Long id,
                                           @PathVariable List<Long> productIDList,
                                           @PathVariable List<Integer> countList,
                                           @PathVariable List<String> stringPriceList) {
        return supplierService.addProductsToStorage(id, productIDList, countList, stringPriceList);
    }
}