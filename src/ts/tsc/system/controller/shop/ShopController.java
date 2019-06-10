package ts.tsc.system.controller.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.controller.parent.NamedControllerWithStorage;
import ts.tsc.system.controller.response.BaseResponseBuilder;
import ts.tsc.system.entity.purchase.Purchase;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.entity.shop.ShopStorage;
import ts.tsc.system.entity.shop.ShopStorageProduct;
import ts.tsc.system.service.base.BaseServiceInterface;
import ts.tsc.system.service.shop.ShopInterface;
import ts.tsc.system.service.storage.manager.StorageServiceInterface;

import java.util.List;

@RestController
@RequestMapping(value = "/app/shop")
public class ShopController
        extends
        NamedControllerWithStorage<Shop,
        ShopInterface,
        Long,
        ShopStorage,
        Purchase,
        ShopStorageProduct> {

    private final ShopInterface shopService;
    private final StorageServiceInterface<ShopStorage, Long> shopStorageService;
    private final BaseServiceInterface<Purchase, Long> purchaseService;

    private final BaseResponseBuilder<Shop> shopBaseResponseBuilder;
    private final BaseResponseBuilder<ShopStorage> shopStorageBaseResponseBuilder;
    private final BaseResponseBuilder<Purchase> purchaseBaseResponseBuilder;

    @Autowired
    public ShopController(ShopInterface shopService,
                                      StorageServiceInterface<ShopStorage, Long> shopStorageService,
                                      BaseServiceInterface<Purchase, Long> purchaseService,
                                      BaseResponseBuilder<Shop> shopBaseResponseBuilder,
                                      BaseResponseBuilder<ShopStorage> shopStorageBaseResponseBuilder,
                                      BaseResponseBuilder<Purchase> purchaseBaseResponseBuilder) {
        this.shopService = shopService;
        this.shopStorageService = shopStorageService;
        this.purchaseService = purchaseService;
        this.shopBaseResponseBuilder = shopBaseResponseBuilder;
        this.shopStorageBaseResponseBuilder = shopStorageBaseResponseBuilder;
        this.purchaseBaseResponseBuilder = purchaseBaseResponseBuilder;
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
     *         7) {@see ShopService#transfer(List, List, ShopStorage, Purchase, Shop)}
     */
    @PostMapping (value = "/order/{shopID}/{productIdList}/{countList}")
    public ResponseEntity<?> receiveOrder(@PathVariable Long shopID,
                                          @PathVariable List<Long> productIdList,
                                          @PathVariable List<Integer> countList) {
        return shopService.receiveOrder(shopID, productIdList, countList);
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
     *         5) код 400 с сообщением NOT_ENOUGH_PRODUCTS - если на складе не хватает какого-нибудь товара
     *         6) код 200 с объектом, если удалось выполнить запрос
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

    @Override
    protected StorageServiceInterface<ShopStorage, Long> getStorageService() { return shopStorageService; }

    @Override
    protected BaseResponseBuilder<ShopStorage> getStorageResponseBuilder() {
        return shopStorageBaseResponseBuilder;
    }

    @Override
    protected BaseServiceInterface<Purchase, Long> getOrderService() {
        return purchaseService;
    }

    @Override
    protected BaseResponseBuilder<Purchase> getOrderResponseBuilder() {
        return purchaseBaseResponseBuilder;
    }

    @Override
    protected BaseResponseBuilder<Shop> getResponseBuilder() {
        return shopBaseResponseBuilder;
    }

    @Override
    protected ShopInterface getService() {
        return shopService;
    }
}

