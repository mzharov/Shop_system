package ts.tsc.system.service.shop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.controller.status.OrderStatus;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.entity.purchase.Purchase;
import ts.tsc.system.entity.purchase.PurchaseProduct;
import ts.tsc.system.entity.purchase.PurchaseProductPrimaryKey;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.entity.shop.ShopStorage;
import ts.tsc.system.entity.shop.ShopStorageProduct;
import ts.tsc.system.entity.shop.ShopStorageProductPrimaryKey;
import ts.tsc.system.repository.purchase.PurchaseProductRepository;
import ts.tsc.system.repository.purchase.PurchaseRepository;
import ts.tsc.system.repository.shop.ShopRepository;
import ts.tsc.system.repository.shop.ShopStorageProductRepository;
import ts.tsc.system.repository.shop.ShopStorageRepository;
import ts.tsc.system.service.named.NamedService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service("shopService")
@Transactional
public class ShopService extends NamedService<Shop, Long> implements ShopInterface {

    private final Logger logger = LoggerFactory.getLogger(ShopService.class);

    private final ShopRepository shopRepository;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseProductRepository purchaseProductRepository;
    private final ShopStorageProductRepository shopStorageProductRepository;
    private final ShopStorageRepository shopStorageRepository;

    @Autowired
    public ShopService(ShopRepository shopRepository,
                       PurchaseRepository purchaseRepository,
                       PurchaseProductRepository purchaseProductRepository,
                       ShopStorageProductRepository shopStorageProductRepository,
                       ShopStorageRepository shopStorageRepository) {
        this.shopRepository = shopRepository;
        this.purchaseRepository = purchaseRepository;
        this.purchaseProductRepository = purchaseProductRepository;
        this.shopStorageProductRepository = shopStorageProductRepository;
        this.shopStorageRepository = shopStorageRepository;
    }

    @Override
    public ShopRepository getRepository() {
        return this.shopRepository;
    }

    /**
     * Обновление состояния объекта
     * @param id идентификатор объекта
     * @param shop тело объекта, содержащее изменения
     * @return 1) измененный объект
     *         2) null - если не удалось произвести сохранение
     */
    @Override
    public Shop update(Long id, Shop shop) {
        return shopRepository.findById(id)
                .map(record -> {
                    record.setName(shop.getName());
                    record.setBudget(shop.getBudget());
                    record.setPurchases(record.getPurchases());
                    record.setStorages(record.getStorages());
                    return shopRepository.save(record);
                }).orElse(null);
    }

    /**
     * Перевод заказа в состояние доставки
     * @param id идентификатор заказа
     * @return 1) код 404 с сообщением ELEMENT_NOT_FOUND:purchase, если заказ не найден
     *         2) код 400 с сообщением WRONG_DELIVERY_STATUS, если нельзя перевести заказ в состояние DELIVERING
     *         3) код 500 с сообщением ERROR_WHILE_SAVING:purchase, если не удалось сохранить изменения
     *         4) код 200 с объектом заказа, если удалось изменить состояние
     */
    @Override
    public ResponseEntity<?> deliverOrder(Long id) {
        Purchase purchase = isPurchaseExist(id);
        if(purchase == null) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND +":purchase",
                    HttpStatus.NOT_FOUND);
        }

        if(!purchase.getOrderStatus().equals(OrderStatus.RECEIVED)) {
            return new ResponseEntity<>(ErrorStatus.WRONG_DELIVERY_STATUS, HttpStatus.BAD_REQUEST);
        }

        purchase.setOrderStatus(OrderStatus.DELIVERING);

        try {
            purchaseRepository.save(purchase);
        } catch (Exception e) {
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":purchase ",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(purchase, HttpStatus.OK);
    }

    /**
     * Перевод заказа в завершенное состояние
     * @param id идентификтаор заказа
     * @return 1) код 404 с сообщением ELEMENT_NOT_FOUND:purchase, если заказ не найден
     *         2) код 400 с сообщением WRONG_DELIVERY_STATUS, если нельзя перевести заказ в состояние COMPLETED
     *         3) код 500 с сообщением ERROR_WHILE_SAVING:purchase, если не удалось сохранить изменения
     *         4) код 200 с объектом заказа, если удалось изменить состояние
     */
    @Override
    public ResponseEntity<?> completeOrder(Long id) {
        Purchase purchase = isPurchaseExist(id);
        if(purchase == null) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":purchase " + id,
                    HttpStatus.NOT_FOUND);
        }

        if(!purchase.getOrderStatus().equals(OrderStatus.DELIVERING)) {
            return new ResponseEntity<>(ErrorStatus.WRONG_DELIVERY_STATUS,
                    HttpStatus.BAD_REQUEST);
        }
        purchase.setOrderStatus(OrderStatus.COMPLETED);

        try {
            purchaseRepository.save(purchase);
        } catch (Exception e) {
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":purchase",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(purchase, HttpStatus.OK);
    }

    /**
     * Отмена заказа
     * @param id идентификтаор заказа
     * @return 1) код 404 с сообщением ELEMENT_NOT_FOUND:purchase, если заказ не найден
     *         2) код 400 с сообщением WRONG_DELIVERY_STATUS, если нельзя перевести заказ в состояние CANCELED
     *         3) код 404 с сообщением ELEMENT_NOT_FOUND:shop_storage, если склад не найден
     *         4) код 400 с сообщением BAD_REQUEST, если не удалось выполнить запрос
     *         5) код 400 с сообщением NOT_ENOUGH_SPACE:shop_storage, если на складе магазина не хватает места
     *         6) код 500 с сообщением ERROR_WHILE_SAVING:purchase, если не удалось сохранить изменения
     *         7) {@link #transfer(List, List, ShopStorage, Purchase, Shop)}
     *
     */
    @Override
    public ResponseEntity<?> cancelOrder(Long id) {
        Purchase purchase = isPurchaseExist(id);
        if(purchase == null) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":purchase " + id,
                    HttpStatus.NOT_FOUND);
        }

        if(isNotCancelable(purchase)) {
            return new ResponseEntity<>(ErrorStatus.WRONG_DELIVERY_STATUS,
                    HttpStatus.BAD_REQUEST);
        }

        purchase.setOrderStatus(OrderStatus.CANCELED);

        Optional<ShopStorage> shopStorageOptional
                = shopStorageRepository.findByShopIdAndType(purchase.getShop().getId(), 1);
        if(!shopStorageOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND
                    + ":shop_storage", HttpStatus.NOT_FOUND);
        }
        ShopStorage shopStorage = shopStorageOptional.get();

        List<Long> productIdList = purchaseProductRepository
                .findPrimaryKeyProductIdByPrimaryKeyPurchaseId(purchase.getId());
        List<Integer> countList = purchaseProductRepository
                .findCountByPrimaryKeyPurchaseId(purchase.getId());

        int productsSumCount = countList.stream().reduce(0, Integer::sum);
        if(shopStorage.getFreeSpace() < productsSumCount) {
            return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_SPACE + ":shop_storage",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            purchaseRepository.save(purchase);
            logger.info(purchase.getOrderStatus().toString());
        } catch (Exception e) {
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":purchase",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Optional<Shop> shopOptional = shopRepository.findById(shopStorage.getShop().getId());
        if(!shopOptional.isPresent()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":shop ",
                    HttpStatus.NOT_FOUND);
        }
        Shop shop = shopOptional.get();

        return transfer(productIdList, countList, shopStorage, purchase, shop);
    }

    /**
     * Обработка поступившего заказа
     * @param shopID идентификатор магазина, в котором запрошена покупка
     * @param productIDList списко идентификтаоров продуктов
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
    public ResponseEntity<?> receiveOrder(Long shopID, List<Long> productIDList, List<Integer> countList) {
        if(productIDList.size() != countList.size()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(ErrorStatus.WRONG_NUMBER_OF_PARAMETERS,
                    HttpStatus.BAD_REQUEST);
        }

        Optional<Shop> shopOptional = findById(shopID);
        if(!shopOptional.isPresent()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":shop",
                    HttpStatus.NOT_FOUND);
        }
        Shop shop = shopOptional.get();


        Optional<ShopStorage> shopStorageOptional
                = shopStorageRepository.findByShopIdAndType(shopID, 1);
        if(!shopStorageOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND
                    + ":storage", HttpStatus.NOT_FOUND);
        }

        ShopStorage shopStorage = shopStorageOptional.get();

        for(int orderIterator = 0; orderIterator < productIDList.size(); orderIterator++) {
            Long productID = productIDList.get(orderIterator);
            Integer count = countList.get(orderIterator);

            Optional<Integer> productCountInStorageOptional
                    = shopStorageProductRepository
                    .findCountByPrimaryKeyStorageIdAndPrimaryKeyProductId(shopStorage.getId(),
                            productID);
            if(!productCountInStorageOptional.isPresent()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND
                        + ":product "
                        + productID,
                        HttpStatus.NOT_FOUND);
            }
            int productCountInStorage = productCountInStorageOptional.get();
            if(productCountInStorage < count) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_PRODUCTS,
                        HttpStatus.BAD_REQUEST);
            }
        }

        Purchase purchase = new Purchase();
        purchase.setShop(shop);
        purchase.setOrderStatus(OrderStatus.RECEIVED);

        try {
            purchaseRepository.save(purchase);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("Error: ", e);
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return transfer(productIDList, countList, shopStorage, purchase, shop);
    }

    /**
     * Оформление заказа RECEIVED или отмена CANCEL
     * @param productIdList  список идентификтаоров товаров
     * @param countList список количества товаров
     * @param shopStorage объект склада магазина
     * @param purchase объект заказа
     * @param shop объект магазина
     * @return 1) код 404 с сообщением NO_PRODUCTS_IN_STORAGE, если товар не найден на складе
     *         2) код 500 с сообщением ERROR_WHILE_SAVING:purchase_product, если не удалось сохранить изменения в заказе
     *         3) код 500 с сообщением ERROR_WHILE_SAVING:shop_storage_product, если не удалось сохранить изменения в записи товара на складе
     *         4) код 500 с сообщением ERROR_WHILE_SAVING:shop_storage если не удалось сохранить изменения на складе
     *         5) код 500 с сообщением ERROR_WHILE_SAVING:shop если не удалось сохранить изменения в магазине
     *         6) код 200 с объектом заказа, если удалось обработать запрос
     */
    private ResponseEntity<?> transfer(List<Long> productIdList, List<Integer> countList, ShopStorage shopStorage, Purchase purchase, Shop shop) {
        for(int orderIterator = 0; orderIterator < productIdList.size(); orderIterator++) {
            Long productID = productIdList.get(orderIterator);
            Integer count = countList.get(orderIterator);

            Optional<ShopStorageProduct> shopStorageProductOptional
                    = shopStorageProductRepository.findByPrimaryKeyStorageIdAndPrimaryKeyProductId
                    (shopStorage.getId(),productID);
            if(!shopStorageProductOptional.isPresent()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ResponseEntity<>(ErrorStatus.NO_PRODUCTS_IN_STORAGE, HttpStatus.NOT_FOUND);
            }
            ShopStorageProduct shopStorageProduct = shopStorageProductOptional.get();


            if(purchase.getOrderStatus().equals(OrderStatus.RECEIVED)) {
                Product product = shopStorageProduct.getPrimaryKey().getProduct();
                PurchaseProduct purchaseProduct = new PurchaseProduct();
                purchaseProduct.setPrimaryKey(new PurchaseProductPrimaryKey(purchase, product));
                purchaseProduct.setCount(count);
                BigDecimal price = shopStorageProduct.getPrice();
                purchaseProduct.setPrice(price);
                BigDecimal sumPrice = price.multiply(new BigDecimal(count));
                purchaseProduct.setSumPrice(sumPrice);

                try {
                    purchaseProductRepository.save(purchaseProduct);
                } catch (Exception e) {
                    logger.error("Error: ", e);
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":purchase",
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

            int coefficient = -1;
            if(purchase.getOrderStatus().equals(OrderStatus.RECEIVED)) {
                coefficient = 1;
            }

            shopStorageProduct.setCount(shopStorageProduct.getCount()-coefficient*count);
            shopStorage.setFreeSpace(shopStorage.getFreeSpace()+coefficient*count);
            shop.setBudget(shop.getBudget().add((shopStorageProduct.getPrice()
                    .multiply(new BigDecimal(count)))
                    .multiply(new BigDecimal(coefficient))));

            try {
                shopStorageProductRepository.save(shopStorageProduct);
            } catch (Exception e) {
                logger.error("Error: ", e);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING
                        + ":shop_storage_product",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        try {
            shopStorageRepository.save(shopStorage);
        } catch (Exception e) {
            logger.error("Error: ", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":shop_storage",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            shopRepository.save(shop);
        } catch (Exception e) {
            logger.error("Error: ", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":shop",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return purchaseRepository.findById(purchase.getId())
                .<ResponseEntity<?>>map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    /**
     * Добавление денег в бюджет магазина
     * @param id идентификатор магазина
     * @param budgetString строковое значение, содержащее количество денег, которое необходимо добавить
     * @return в случае успешного завершения возвращается объект типа Shop c кодом 200,
     * если магазин не найден - код 404,
     * если не удалось перевести входную строку в BigDecimal - код 400 с сообщением NUMBER_FORMAT_EXCEPTION,
     * если в ходе сохранения объекта произошла ошибка возвращается код 500 с сообщением ERROR_WHILE_SAVING
     */
    @Override
    public ResponseEntity<?> addBudget(Long id, String budgetString) {
        Optional<Shop> shopOptional = shopRepository.findById(id);
        if(!shopOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND,
                    HttpStatus.NOT_FOUND);
        }
        BigDecimal budget;
        try {
            budget = new BigDecimal(budgetString).setScale(5, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return new ResponseEntity<>(ErrorStatus.NUMBER_FORMAT_EXCEPTION,
                    HttpStatus.BAD_REQUEST);
        }

        Shop shop = shopOptional.get();
        shop.setBudget(shop.getBudget().add(budget));
        try {
            shopRepository.save(shop);
        } catch (Exception e) {
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(shop, HttpStatus.OK);
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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> transferProducts(Long shopStorageID, Long targetShopStorageID, List<Long> productIDList, List<Integer> countList) {
        if(productIDList.size() != countList.size()) {
            return new ResponseEntity<>(ErrorStatus.WRONG_NUMBER_OF_PARAMETERS,
                    HttpStatus.BAD_REQUEST);
        }

        Optional<ShopStorage> targetShopStorageOptional
                = shopStorageRepository.findById(targetShopStorageID);
        Optional<ShopStorage> shopStorageOptional
                = shopStorageRepository.findById(shopStorageID);

        if(!shopStorageOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":source_storage",
                    HttpStatus.NOT_FOUND);
        }
        if(!targetShopStorageOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":target_storage",
                    HttpStatus.NOT_FOUND);
        }

        ShopStorage targetShopStorage = targetShopStorageOptional.get();
        ShopStorage shopStorage = shopStorageOptional.get();

        int sumCount = countList.stream().reduce(0, Integer::sum);
        if(targetShopStorage.getFreeSpace() < sumCount) {
            return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_SPACE + ":target_storage",
                    HttpStatus.BAD_REQUEST);
        }

        for(int queryIterator = 0; queryIterator < productIDList.size(); queryIterator++) {
            Long productID = productIDList.get(queryIterator);
            Integer count = countList.get(queryIterator);


            Optional<ShopStorageProduct> shopStorageProductOptional
                    = shopStorageProductRepository
                    .findByPrimaryKeyStorageIdAndPrimaryKeyProductId(shopStorageID, productID);
            if(!shopStorageProductOptional.isPresent()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":product "
                        + productID,
                        HttpStatus.FOUND);
            }

            ShopStorageProduct shopStorageProduct = shopStorageProductOptional.get();
            if(count > shopStorageProduct.getCount()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_PRODUCTS
                        + ":source_storage",
                        HttpStatus.BAD_REQUEST);
            }

            ShopStorageProduct shopStorageProductMain;
            Optional<ShopStorageProduct> shopStorageProductMainOptional
                    = shopStorageProductRepository
                    .findByPrimaryKeyStorageIdAndPrimaryKeyProductId(targetShopStorageID, productID);
            if(shopStorageProductMainOptional.isPresent()) {
                shopStorageProductMain = shopStorageProductMainOptional.get();
                shopStorageProductMain.setCount(shopStorageProductMain.getCount() + count);
            } else {
                shopStorageProductMain = new ShopStorageProduct();
                shopStorageProductMain
                        .setPrimaryKey(new ShopStorageProductPrimaryKey(targetShopStorage,
                                shopStorageProduct.getPrimaryKey().getProduct()));
                shopStorageProductMain.setCount(count);
                shopStorageProductMain.setPrice(shopStorageProduct.getPrice());
            }

            shopStorageProduct.setCount(shopStorageProduct.getCount()-count);
            shopStorage.setFreeSpace(shopStorage.getFreeSpace()+count);
            targetShopStorage.setFreeSpace(targetShopStorage.getFreeSpace()-count);

            try {
                shopStorageProductRepository.save(shopStorageProductMain);
                shopStorageProductRepository.save(shopStorageProduct);
                shopStorageRepository.save(shopStorage);
                shopStorageRepository.save(targetShopStorage);
            } catch (Exception e) {
                logger.error("Error", e);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return shopStorageRepository.findById(targetShopStorageID)
                .<ResponseEntity<?>>map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private Purchase isPurchaseExist(Long id) {
        Optional<Purchase> deliveryOptional = purchaseRepository.findById(id);
        return deliveryOptional.orElse(null);
    }

    private boolean isNotCancelable(Purchase purchase) {
        return !(purchase.getOrderStatus().equals(OrderStatus.RECEIVED)
                || purchase.getOrderStatus().equals(OrderStatus.DELIVERING));
    }
}
