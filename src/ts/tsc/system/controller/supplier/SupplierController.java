package ts.tsc.system.controller.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.controller.parent.BaseControllerWithStorage;
import ts.tsc.system.controller.response.BaseResponseBuilder;
import ts.tsc.system.entity.delivery.Delivery;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.entity.supplier.SupplierStorage;
import ts.tsc.system.entity.supplier.SupplierStorageProduct;
import ts.tsc.system.service.base.BaseServiceInterface;
import ts.tsc.system.service.supplier.SupplierInterface;
import ts.tsc.system.service.storage.manager.StorageServiceInterface;

import java.util.List;

@RestController
@RequestMapping(value = "/app/supplier")
public class SupplierController
        extends BaseControllerWithStorage<Supplier, SupplierInterface, Long,
        SupplierStorage, Delivery, SupplierStorageProduct> {

    private final SupplierInterface supplierService;
    private final BaseServiceInterface<Delivery, Long> deliveryService;
    private final StorageServiceInterface<SupplierStorage, Long> supplierStorageService;

    private final BaseResponseBuilder<Supplier> supplierBaseResponseBuilder;
    private final BaseResponseBuilder<Delivery> deliveryBaseResponseBuilder;
    private final BaseResponseBuilder<SupplierStorage> supplierStorageBaseResponseBuilder;

    @Autowired
    public SupplierController(BaseServiceInterface<Delivery, Long> deliveryService,
                              StorageServiceInterface<SupplierStorage, Long> supplierStorageService,
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

    @Override
    protected StorageServiceInterface<SupplierStorage, Long> getStorageService() {
        return supplierStorageService;
    }

    @Override
    protected BaseResponseBuilder<SupplierStorage> getStorageResponseBuilder() {
        return supplierStorageBaseResponseBuilder;
    }

    @Override
    protected BaseServiceInterface<Delivery, Long> getOrderService() {
        return deliveryService;
    }

    @Override
    protected BaseResponseBuilder<Delivery> getOrderResponseBuilder() {
        return deliveryBaseResponseBuilder;
    }

    @Override
    protected BaseResponseBuilder<Supplier> getResponseBuilder() {
        return supplierBaseResponseBuilder;
    }

    @Override
    protected SupplierInterface getService() {
        return supplierService;
    }
}