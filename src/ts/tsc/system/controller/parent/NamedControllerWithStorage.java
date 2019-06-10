package ts.tsc.system.controller.parent;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.controller.aspect.IDValidation;
import ts.tsc.system.controller.response.BaseResponseBuilder;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.controller.status.OrderStatus;
import ts.tsc.system.entity.parent.BaseEntity;
import ts.tsc.system.entity.parent.BaseStorage;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.service.base.BaseService;
import ts.tsc.system.service.base.BaseServiceInterface;
import ts.tsc.system.service.order.OrderInterface;
import ts.tsc.system.service.storage.manager.StorageServiceInterface;
import ts.tsc.system.service.storage.manager.StorageServiceManager;

import java.util.Optional;

public abstract class NamedControllerWithStorage
        <ENTITY extends NamedEntity<ID>,
                SERVICE extends OrderInterface<ENTITY, ID>,
                ID,
                STORAGE extends BaseEntity<ID> & BaseStorage<ENTITY, STORAGE_PRODUCT>,
                ORDER,
                STORAGE_PRODUCT>
        extends NamedController<ENTITY, SERVICE, ID> {


    /**
     * Список всех складов
     * @return {@link BaseService#findAll()}
     */
    @GetMapping(value = "/storage/list")
    public ResponseEntity<?>  findAllStorage() {
        return getStorageResponseBuilder().getAll(getStorageService().findAll());
    }


    /**
     * Поиск заказа по идентификатору
     * @param id идентификатор заказа
     * @return {@link BaseService#findById(Object)}
     */
    @GetMapping(value = "/order/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable ID id) {
        Optional<ORDER> purchaseOptional = getOrderService().findById(id);
        return purchaseOptional.<ResponseEntity<?>>map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    /**
     * Поиск склада по идентификатору
     * @param id идентификатор склада
     * @return {@link StorageServiceManager#findById(Object)}
     */
    @GetMapping(value = "/storage/{id}")
    public ResponseEntity<?> findStorageById(@PathVariable ID id) {
        Optional<STORAGE> purchaseOptional = getStorageService().findById(id);
        return purchaseOptional.<ResponseEntity<?>>map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    /**
     * Поиск складов по идентификтору владельца
     * @param id идентификтаор магазина
     * @return {@link StorageServiceManager#findStoragesByOwnerId(Object)}
     */
    @GetMapping(value = "/{id}/storage/list")
    public ResponseEntity<?> findStoragesByOwnerId(@PathVariable ID id) {
        return getOrderResponseBuilder().getAll(getStorageService().findStoragesByOwnerId(id));
    }

    /**
     * Добавление склада владельцу
     * @param id идентификатор магазина
     * @param storage объект типа Storage, который будет добавлен
     * @return  1) код 400 с сообщением ID_CAN_NOT_BE_SET_IN_JSON, если в теле json задан идентификатор
     *          2) {@link StorageServiceManager#addStorage(Object, BaseStorage)}
     */
    @PostMapping(value = "/storage/{id}")
    @IDValidation
    public ResponseEntity<?> addStorage(@PathVariable ID id, @RequestBody STORAGE storage) {
        return getStorageService().addStorage(id, storage);
    }


    /**
     * Получение списка продуктов со склада
     * @param id идентификтаор склада
     * @return 1) склад с указанным идентификатором найден возвращается список товаров с колом 200,
     *         2) если товаров на складе нет возвращается код 404 с сообщением NO_PRODUCTS_IN_STORAGE,
     *         3) если склад с указанным идентификатором не найден код 404 c сообщением ELEMENT_NOT_FOUND
     */
    @GetMapping(value = "/storage/{id}/product/list")
    public ResponseEntity<?> getStorageProducts(@PathVariable ID id) {
        Optional<STORAGE> storageOptional = getStorageService().findById(id);
        if(!storageOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND,
                    HttpStatus.NOT_FOUND);
        }
        STORAGE storage = storageOptional.get();
        if(storage.getProducts().size() > 0) {
            return ResponseEntity.ok().body(storage.getProducts());
        } else {
            return new ResponseEntity<>(ErrorStatus.NO_PRODUCTS_IN_STORAGE,
                    HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Получение списка заказов
     * @return {@link BaseService#findAll()}
     */
    @GetMapping(value = "/order/list")
    public ResponseEntity<?> getAllOrders() {
        return getOrderResponseBuilder().getAll(getOrderService().findAll());
    }

    /**
     * Изменения статуса заказа
     * @param id идентификатор заказа
     * @param status новое состояние
     * @return 1) объект заказа с кодом 200, если успешно,
     *         2) код 400 с описанием UNKNOWN_DELIVER_STATUS, если передано неизвестное состояние,
     *         3) Возможные значения, возвращаемые из методов:
     *                      3.1) {@link ts.tsc.system.service.shop.ShopService#deliverOrder(Long)}
     *                      3.2) {@link ts.tsc.system.service.supplier.SupplierService#deliverOrder(Long)}
     *                      3.3) {@link ts.tsc.system.service.shop.ShopService#completeOrder(Long)} (Long)}
     *                      3.4) {@link ts.tsc.system.service.supplier.SupplierService#completeOrder(Long)}
     *                      3.5) {@link ts.tsc.system.service.shop.ShopService#cancelOrder(Long)} (Long)}
     *                      3.6) {@link ts.tsc.system.service.supplier.SupplierService#cancelOrder(Long)}
     *        4) код 400 с сообщением WRONG_DELIVERY_STATUS
     */
    @PutMapping(value = "/order/{id}/status/{status}")
    public ResponseEntity<?> changeStatus(@PathVariable ID id, @PathVariable String status) {
        OrderStatus orderStatus;

        try {
            orderStatus = OrderStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ErrorStatus.UNKNOWN_DELIVER_STATUS, HttpStatus.BAD_REQUEST);
        }

        if(orderStatus.equals(OrderStatus.DELIVERING)) {
            return getService().deliverOrder(id);
        }
        if(orderStatus.equals(OrderStatus.COMPLETED)) {
            return getService().completeOrder(id);
        }
        if(orderStatus.equals(OrderStatus.CANCELED)) {
            return getService().cancelOrder(id);
        }
        return new ResponseEntity<>(ErrorStatus.WRONG_DELIVERY_STATUS, HttpStatus.BAD_REQUEST);
    }


    abstract protected StorageServiceInterface<STORAGE, ID> getStorageService();
    abstract protected BaseResponseBuilder<STORAGE> getStorageResponseBuilder();
    abstract protected BaseServiceInterface<ORDER, ID> getOrderService();
    abstract protected BaseResponseBuilder<ORDER> getOrderResponseBuilder();
}
