package ts.tsc.system.service.supplier;

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
import ts.tsc.system.entity.delivery.Delivery;
import ts.tsc.system.entity.delivery.DeliveryProduct;
import ts.tsc.system.entity.delivery.DeliveryProductPrimaryKey;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.entity.shop.ShopStorage;
import ts.tsc.system.entity.shop.ShopStorageProduct;
import ts.tsc.system.entity.shop.ShopStorageProductPrimaryKey;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.entity.supplier.SupplierStorage;
import ts.tsc.system.entity.supplier.SupplierStorageProduct;
import ts.tsc.system.entity.supplier.SupplierStorageProductPrimaryKey;
import ts.tsc.system.repository.delivery.DeliveryProductRepository;
import ts.tsc.system.repository.delivery.DeliveryRepository;
import ts.tsc.system.repository.shop.ShopRepository;
import ts.tsc.system.repository.shop.ShopStorageProductRepository;
import ts.tsc.system.repository.shop.ShopStorageRepository;
import ts.tsc.system.repository.supplier.SupplierRepository;
import ts.tsc.system.repository.supplier.SupplierStorageProductRepository;
import ts.tsc.system.repository.supplier.SupplierStorageRepository;
import ts.tsc.system.service.named.NamedService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service("supplierService")
@Transactional
public class SupplierService extends NamedService<Supplier, Long> implements SupplierInterface {

    @PersistenceContext
    EntityManager entityManager;

    private final Logger logger = LoggerFactory.getLogger(SupplierService.class);

    private final SupplierRepository supplierRepository;
    private final SupplierStorageRepository supplierStorageRepository;
    private final SupplierStorageProductRepository supplierStorageProductRepository;
    private final DeliveryRepository deliveryRepository;
    private final ShopRepository shopRepository;
    private final DeliveryProductRepository deliveryProductRepository;
    private final ShopStorageProductRepository shopStorageProductRepository;
    private final ShopStorageRepository shopStorageRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository,
                           SupplierStorageRepository supplierStorageRepository,
                           SupplierStorageProductRepository supplierStorageProductRepository,
                           DeliveryRepository deliveryRepository, ShopRepository shopRepository,
                           DeliveryProductRepository deliveryProductRepository,
                           ShopStorageProductRepository shopStorageProductRepository,
                           ShopStorageRepository shopStorageRepository) {
        this.supplierRepository = supplierRepository;
        this.supplierStorageRepository = supplierStorageRepository;
        this.supplierStorageProductRepository = supplierStorageProductRepository;
        this.deliveryRepository = deliveryRepository;
        this.shopRepository = shopRepository;
        this.deliveryProductRepository = deliveryProductRepository;
        this.shopStorageProductRepository = shopStorageProductRepository;
        this.shopStorageRepository = shopStorageRepository;
    }


    @Override
    public SupplierRepository getRepository() {
        return this.supplierRepository;
    }

    /**
     * Обновление объекта
     * @param id идентификатор объекта
     * @param supplier измененный объект
     * @return 1) измененный объект после сохранения в БД
     *         2) null - если произошла ошибка входе сохранения
     */
    @Override
    public Supplier update(Long id, Supplier supplier) {
        return supplierRepository.findById(id)
                .map(record -> {
                    record.setName(supplier.getName());
                    record.setStorages(supplier.getStorages());
                    return supplierRepository.save(record);
                }).orElse(null);
    }

    /**
     * Перевод заказа в состояние DELIVERING
     * @param id идентификтаор заказа
     * @return 1) код 404 с сообщением ELEMENT_NOT_FOUND:deliver, если заказ не найден
     *         2) код 400 с сообщением WRONG_DELIVERY_STATUS, если нельзя перевести заказ в состояние COMPLETED
     *         3) код 500 с сообщением ERROR_WHILE_SAVING:deliver, если не удалось сохранить изменения
     *         4) код 200 с объектом заказа, если удалось изменить состояние
     */
    @Override
    public ResponseEntity<?> deliverOrder(Long id) {
        Delivery delivery = isExist(id);
        if(delivery == null) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":deliver",
                    HttpStatus.NOT_FOUND);
        }

        if(!delivery.getOrderStatus().equals(OrderStatus.RECEIVED)) {
            return new ResponseEntity<>(ErrorStatus.WRONG_DELIVERY_STATUS, HttpStatus.BAD_REQUEST);
        }

        delivery.setOrderStatus(OrderStatus.DELIVERING);

        try {
            deliveryRepository.save(delivery);
        } catch (Exception e) {
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":delivery",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(delivery, HttpStatus.OK);
    }

    /**
     * Перевод заказа в завершенное состояние
     * @param id идентификтаор заказа
     * @return 1) код 404 с сообщением ELEMENT_NOT_FOUND:delivery, если заказ не найден
     *         2) код 400 с сообщением BAD_REQUEST, если не удалось выполнить запрос
     *         3) код 400 с сообщением WRONG_DELIVERY_STATUS, если нельзя перевести заказ в состояние COMPLETED
     *         4) код 500 с сообщением ERROR_WHILE_SAVING:delivery, если не удалось сохранить изменения
     *         5) код 400 с сообщением NOT_ENOUGH_SPACE:shop_storage, если на складе магазина не хватает места
     *         6) код 500 с сообщением ERROR_WHILE_SAVING:shop_storage_product, если не удалось сохранить изменения в записи товара со склада
     *         7) код 500 с сообщением ERROR_WHILE_SAVING:shop_storage, если не удалось сохранить изменения в записи склада
     *         8) код 500 с сообщением ERROR_WHILE_SAVING:delivery, если не удалось сохранить изменения в заказе
     *         9) код 200 с объектом заказа, если удалось завершить запрос
     */
    @Override
    public ResponseEntity<?> completeOrder(Long id) {
        Optional<Delivery> deliveryOptional = deliveryRepository.findById(id);

        if(!deliveryOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":delivery",
                    HttpStatus.NOT_FOUND);
        }

        Delivery delivery = deliveryOptional.get();

        if(!delivery.getOrderStatus().equals(OrderStatus.DELIVERING)) {
            return new ResponseEntity<>(ErrorStatus.WRONG_DELIVERY_STATUS,
                    HttpStatus.BAD_REQUEST);
        }

        delivery.setOrderStatus(OrderStatus.COMPLETED);
        ShopStorage shopStorage = delivery.getShopStorage();
        Long sumProductCount = 0L;

        try {
            TypedQuery<Long> sumProductTypedQuery = entityManager.createQuery(
                    "select sum(p.count) from DeliveryProduct p " +
                            "where p.primaryKey.delivery.id = ?1",
                    Long.class).setParameter(1, delivery.getId());
            sumProductCount = sumProductTypedQuery.getSingleResult();
        } catch (Exception e) {
            logger.error("Error", e);
            new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":delivery", HttpStatus.NOT_FOUND);
        }

        if(shopStorage.getFreeSpace() < sumProductCount) {
            return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_SPACE + ":shop_storage",
                    HttpStatus.BAD_REQUEST);
        }

        List<DeliveryProduct> deliveryProductList = new LinkedList<>();
        try {
            TypedQuery<DeliveryProduct> deliveryProductQuery =
                    entityManager.createQuery(
                            "select p from DeliveryProduct p " +
                                    "where p.primaryKey.delivery.id = ?1",
                            DeliveryProduct.class)
                            .setParameter(1, delivery.getId());
            deliveryProductList = deliveryProductQuery.getResultList();
        } catch (Exception e) {
            logger.error("Error", e);
            new ResponseEntity<>(ErrorStatus.BAD_QUERY,
                    HttpStatus.NOT_FOUND);
        }

        for(DeliveryProduct deliveryProduct : deliveryProductList) {
            ShopStorageProductPrimaryKey primaryKey = new ShopStorageProductPrimaryKey(shopStorage,
                    deliveryProduct.getPrimaryKey().getProduct());

            Optional<ShopStorageProduct> shopStorageProductOptional
                    = shopStorageProductRepository
                    .findById(primaryKey);
            ShopStorageProduct shopStorageProduct;
            if(shopStorageProductOptional.isPresent()) {
                shopStorageProduct = shopStorageProductOptional.get();
                shopStorageProduct
                        .setCount(shopStorageProduct.getCount()+deliveryProduct.getCount());

            } else {
                shopStorageProduct  = new ShopStorageProduct();
                shopStorageProduct.setPrimaryKey(primaryKey);
                shopStorageProduct.setCount(deliveryProduct.getCount());
                shopStorageProduct.setPrice(deliveryProduct.getPrice());
            }
            shopStorage.setFreeSpace(shopStorage.getFreeSpace()-deliveryProduct.getCount());
            try {
                shopStorageProductRepository.save(shopStorageProduct);
            } catch (Exception e) {
                logger.error("Error", e);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":shop_storage_product",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            try {
                shopStorageRepository.save(shopStorage);
            } catch (Exception e) {
                logger.error("Error", e);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":shop_storage",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        try {
            deliveryRepository.save(delivery);
        } catch (Exception e) {
            logger.error("Error", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":delivery",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(delivery, HttpStatus.OK);
    }

    /**
     * Отмена заказа
     * @param id идентификатор заказа
     * @return 1) код 404 с сообщением ELEMENT_NOT_FOUND:delivery, если заказ не найден
     *         2) код 400 с сообщением WRONG_DELIVERY_STATUS, если нельзя перевести заказ в состояние CANCELED
     *         3) код 404 с сообщением ELEMENT_NOT_FOUND:supplier_storage, если склад не найден
     *         4) код 400 с сообщением BAD_REQUEST, если не удалось выполнить запрос
     *         5) код 400 с сообщением NOT_ENOUGH_SPACE:shop_storage, если на складе магазина не хватает места
     *         6) код 500 с сообщением ERROR_WHILE_SAVING:purchase, если не удалось сохранить изменения
     *         7) код 404 с сообщением ELEMENT_NOT_FOUND:shop, если магазин не найден
     *         8) {@link #transfer(List, List, SupplierStorage, Delivery, Shop)}
     */
    @Override
    public ResponseEntity<?> cancelOrder(Long id) {
        Delivery delivery = isExist(id);
        if(delivery == null) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":delivery",
                    HttpStatus.NOT_FOUND);
        }

        if(isNotCancelable(delivery)) {
            return new ResponseEntity<>(ErrorStatus.WRONG_DELIVERY_STATUS,
                    HttpStatus.BAD_REQUEST);
        }
        delivery.setOrderStatus(OrderStatus.CANCELED);


        Optional<SupplierStorage> supplierStorageOptional
                = supplierStorageRepository.findById(delivery.getSupplierStorage().getId());
        if(!supplierStorageOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":supplier_storage",
                    HttpStatus.NOT_FOUND);
        }

        List<Long> productIdList = new LinkedList<>();
        List<Integer> countList = new LinkedList<>();

        try {
            TypedQuery<DeliveryProduct> sumCountTypedQuery = entityManager.createQuery(
                    "select p from DeliveryProduct p " +
                            "where p.primaryKey.delivery.id = ?1",
                    DeliveryProduct.class)
                    .setParameter(1, delivery.getId());

            List<DeliveryProduct> deliveryProductList = sumCountTypedQuery.getResultList();
            for(DeliveryProduct deliveryProduct : deliveryProductList) {
                productIdList.add(deliveryProduct.getPrimaryKey().getProduct().getId());
                countList.add(deliveryProduct.getCount());
            }
        } catch (Exception e) {
            logger.error("Error: ", e);
            return new ResponseEntity<>(ErrorStatus.BAD_QUERY,
                    HttpStatus.BAD_REQUEST);
        }

        int productsSumCount = countList.stream().reduce(0, Integer::sum);

        SupplierStorage supplierStorage = supplierStorageOptional.get();
        if(supplierStorage.getFreeSpace() < productsSumCount) {
            return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_SPACE + ":supplier_storage",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            deliveryRepository.save(delivery);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":delivery",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Optional<Shop> shopOptional
                = shopRepository.findById(delivery.getShopStorage().getShop().getId());
        if(!shopOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":shop",
                    HttpStatus.NOT_FOUND);
        }
        Shop shop = shopOptional.get();

        return transfer(productIdList, countList, supplierStorage, delivery, shop);
    }

    /**
     * Оформление заказа RECEIVED или отмена CANCEL
     * @param productIdList список идентификаторов продуктов
     * @param countList список количества товаров
     * @param supplierStorage объект кслада поставщика
     * @param delivery объект заказа
     * @param shop объект магазина
     * @return 1) код 404 с сообщением NO_PRODUCTS_IN_STORAGE, если товар не найден на складе
     *         2) код 500 с сообщением ERROR_WHILE_SAVING:delivery_product, если не удалось сохранить изменения в заказе
     *         3) код 500 с сообщением ERROR_WHILE_SAVING:supplier_storage_product если не удалось сохранить изменения в записи товара на складе
     *         4) код 500 с сообщением ERROR_WHILE_SAVING:supplier_storage если не удалось сохранить изменения на складе
     *         5) код 500 с сообщением ERROR_WHILE_SAVING:shop если не удалось сохранить изменения в магазине
     *         6) код 200 с объектом заказа, если удалось обработать запрос
     */
    private ResponseEntity<?> transfer(List<Long> productIdList,
                                       List<Integer> countList,
                                       SupplierStorage supplierStorage,
                                       Delivery delivery, Shop shop) {
        for(int deliveryIterator = 0; deliveryIterator < productIdList.size(); deliveryIterator++) {
            Long productID = productIdList.get(deliveryIterator);
            Integer count = countList.get(deliveryIterator);
            SupplierStorageProduct supplierStorageProduct;

            try {
                TypedQuery<SupplierStorageProduct> sumCountTypedQuery = entityManager.createQuery(
                        "select p from SupplierStorageProduct p " +
                                "where p.primaryKey.storage.id = ?1 " +
                                "and p.primaryKey.product.id = ?2",
                        SupplierStorageProduct.class)
                        .setParameter(1, supplierStorage.getId())
                        .setParameter(2, productID);
                supplierStorageProduct = sumCountTypedQuery.getSingleResult();
            } catch (Exception e) {
                logger.error("Error", e);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ResponseEntity<>(ErrorStatus.NO_PRODUCTS_IN_STORAGE, HttpStatus.NOT_FOUND);
            }

            if(delivery.getOrderStatus().equals(OrderStatus.RECEIVED)) {
                DeliveryProduct deliveryProduct = new DeliveryProduct();
                deliveryProduct
                        .setPrimaryKey(new DeliveryProductPrimaryKey
                                (delivery, supplierStorageProduct.getPrimaryKey().getProduct()));
                deliveryProduct.setPrice(supplierStorageProduct.getPrice());
                deliveryProduct.setCount(count);
                deliveryProduct
                        .setSumPrice(supplierStorageProduct.getPrice()
                                .multiply(new BigDecimal(count)));
                try {
                    deliveryProductRepository.save(deliveryProduct);
                } catch (Exception e) {
                    logger.error("Error: ", e);
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":delivery_product" + productID,
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

            int coefficient = -1;
            if(delivery.getOrderStatus().equals(OrderStatus.RECEIVED)) {
                coefficient = 1;
            }

            supplierStorage.setFreeSpace(supplierStorage.getFreeSpace() + coefficient * count);
            supplierStorageProduct.setCount(supplierStorageProduct.getCount() - coefficient* count);
            shop.setBudget(shop.getBudget().subtract((supplierStorageProduct.getPrice()
                    .multiply(new BigDecimal(count)))
                    .multiply(new BigDecimal(coefficient))));
            try {
                supplierStorageProductRepository.save(supplierStorageProduct);
            } catch (Exception e) {
                logger.error("Error: ", e);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":supplier_storage_product",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        try {
            supplierStorageRepository.save(supplierStorage);
        } catch (Exception e) {
            logger.error("Error: ", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":supplier_storage",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            shopRepository.save(shop);
        } catch (Exception e) {
            logger.error("Error: ", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":shop",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return deliveryRepository.findById(delivery.getId())
                .<ResponseEntity<?>>map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
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
     *         7) код 500 с сообщением BAD_QUERY, если не удалось выполнить запрос
     *         8) код 404 с сообщением ELEMENT_NOT_FOUND:shop, если не удалось найти магазин
     *         9) код 400 с сообщением NOT_ENOUGH_MONEY, если у магазина не хватает бюджета
     *         10) код 500 с сообщением ERROR_WHILE_SAVING, если не удалось сохранить заказ
     *         11) {@link #transfer(List, List, SupplierStorage, Delivery, Shop)}
     *
     */
    @Override
    public ResponseEntity<?> receiveOrder(Long supplierID,
                                          Long shopStorageID,
                                          List<Long> productIdList,
                                          List<Integer> countList) {
        if(productIdList.size() != countList.size()) {
            return new ResponseEntity<>(ErrorStatus.WRONG_NUMBER_OF_PARAMETERS,
                    HttpStatus.BAD_REQUEST);
        }

        Optional<SupplierStorage> supplierStorageOptional
                = supplierStorageRepository.findById(supplierID);
        if(!supplierStorageOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":supplier_storage",
                    HttpStatus.NOT_FOUND);
        }

        Optional<ShopStorage> shopStorageOptional
                = shopStorageRepository.findById(shopStorageID);
        if(!shopStorageOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":shop_storage",
                    HttpStatus.NOT_FOUND);
        }

        SupplierStorage supplierStorage = supplierStorageOptional.get();
        ShopStorage shopStorage = shopStorageOptional.get();

        int productsSumCount = countList.stream().reduce(0, Integer::sum);

        if(shopStorage.getFreeSpace() < productsSumCount) {
            return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_SPACE,
                    HttpStatus.BAD_REQUEST);
        }

        for(int deliveryIterator = 0; deliveryIterator < productIdList.size(); deliveryIterator++) {
            Long productID = productIdList.get(deliveryIterator);
            Integer count = countList.get(deliveryIterator);
            try {
                TypedQuery<Integer> sumCountTypedQuery = entityManager.createQuery(
                        "select p.count from SupplierStorageProduct p " +
                                "where p.primaryKey.storage.id = ?1 " +
                                "and p.primaryKey.product.id = ?2",
                        Integer.class)
                        .setParameter(1, supplierStorage.getId())
                        .setParameter(2, productID);
                Integer sumCount = sumCountTypedQuery.getSingleResult();
                if(sumCount < count) {
                    return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_PRODUCTS,
                            HttpStatus.BAD_REQUEST);
                }
            } catch (Exception e) {
                logger.error("Error: ", e);
                return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":product",
                        HttpStatus.NOT_FOUND);
            }
        }

        BigDecimal sumPrice = new BigDecimal(0);

        for (Long productID : productIdList) {
            try {
                TypedQuery<BigDecimal> sumCountTypedQuery = entityManager.createQuery(
                        "select sum(p.price) from SupplierStorageProduct p " +
                                "where p.primaryKey.storage.id = ?1 " +
                                "and p.primaryKey.product.id = ?2",
                        BigDecimal.class)
                        .setParameter(1, supplierStorage.getId())
                        .setParameter(2, productID);
                sumPrice = sumCountTypedQuery.getSingleResult();
            } catch (Exception e) {
                logger.error("Error: ", e);
                return new ResponseEntity<>(ErrorStatus.BAD_QUERY,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        Optional<Shop> shopOptional = shopRepository.findById(shopStorage.getShop().getId());
        if(!shopOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":shop",
                    HttpStatus.NOT_FOUND);
        }

        Shop shop = shopOptional.get();

        if(sumPrice.compareTo(shop.getBudget()) > 0) {
            return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_MONEY,
                    HttpStatus.BAD_REQUEST);
        }

        Delivery delivery = new Delivery();
        delivery.setOrderStatus(OrderStatus.RECEIVED);
        delivery.setShopStorage(shopStorage);
        delivery.setSupplierStorage(supplierStorage);

        try {
            deliveryRepository.save(delivery);
        } catch (Exception e) {
            logger.error("Error: ", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return transfer(productIdList, countList, supplierStorage, delivery, shop);
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
    @Override
    public ResponseEntity<?> addProductsToStorage(Long id, List<Long> productIDList, List<Integer> countList, List<String> stringPriceList) {
        if((productIDList.size() != countList.size())
                || (productIDList.size() != stringPriceList.size())) {
            return new ResponseEntity<>(ErrorStatus.WRONG_NUMBER_OF_PARAMETERS,
                    HttpStatus.BAD_REQUEST);
        }

        List<BigDecimal> priceList = new LinkedList<>();

        for(String stringPrice : stringPriceList) {
            try {
                BigDecimal decimal = new BigDecimal(stringPrice).setScale(5, RoundingMode.HALF_UP);
                priceList.add(decimal);
            } catch (Exception e) {
                return new ResponseEntity<>(ErrorStatus.NUMBER_FORMAT_EXCEPTION,
                        HttpStatus.BAD_REQUEST);
            }
        }


        Optional<SupplierStorage> supplierStorageOptional = supplierStorageRepository.findById(id);
        if(!supplierStorageOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":supplier_storage",
                    HttpStatus.NOT_FOUND);
        }

        SupplierStorage supplierStorage = supplierStorageOptional.get();
        int countSum = countList.stream().reduce(0, Integer::sum);
        if(supplierStorage.getFreeSpace() < countSum) {
            return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_SPACE + "", HttpStatus.BAD_REQUEST);
        }

        List<SupplierStorageProduct> supplierStorageProductList = new LinkedList<>();
        for(int iterator = 0; iterator < productIDList.size(); iterator++) {
            Long productID = productIDList.get(iterator);
            Integer count = countList.get(iterator);
            BigDecimal price = priceList.get(iterator);

            Product product;

            try {
                TypedQuery<Product> productTypedQuery =
                        entityManager.createQuery(
                                "select  p from Product p " +
                                        "where p.id = ?1",
                                Product.class).setParameter(1, productID);
                product = productTypedQuery.getSingleResult();
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":product " + productID,
                        HttpStatus.NOT_FOUND);
            }

            SupplierStorageProduct supplierStorageProduct;
            try {
                TypedQuery<SupplierStorageProduct> supplierStorageProductTypedQuery
                        =  entityManager.createQuery(
                        "select p from SupplierStorageProduct p " +
                                "where p.primaryKey.product.id =?1 " +
                                "and p.primaryKey.storage.id = ?2",
                        SupplierStorageProduct.class)
                        .setParameter(1, productID)
                        .setParameter(2, supplierStorage.getId());
                supplierStorageProduct = supplierStorageProductTypedQuery.getSingleResult();
                supplierStorageProduct.setPrice(price);
                supplierStorageProduct.setCount(supplierStorageProduct.getCount()+count);

            } catch (Exception e) {
                supplierStorageProduct = new SupplierStorageProduct();
                supplierStorageProduct
                        .setPrimaryKey(new SupplierStorageProductPrimaryKey(supplierStorage, product));
                supplierStorageProduct.setCount(count);
                supplierStorageProduct.setPrice(price);
                supplierStorage.addProducts(supplierStorageProduct);
            }
            supplierStorage.setFreeSpace(supplierStorage.getFreeSpace()-count);
            supplierStorageProductList.add(supplierStorageProduct);
        }

        for(SupplierStorageProduct supplierStorageProduct : supplierStorageProductList) {
            try {
                supplierStorageProductRepository.save(supplierStorageProduct);
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":supplier_storage_product",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        try {
            supplierStorageRepository.save(supplierStorage);
        } catch (Exception e) {
            logger.error("Error ", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + ":supplier_storage",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        supplierStorageOptional = supplierStorageRepository.findById(id);
        return supplierStorageOptional.<ResponseEntity<?>>map(supplierStorageR ->
                new ResponseEntity<>(supplierStorageR, HttpStatus.OK)).orElseGet(()
                -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private Delivery isExist(Long id) {
        Optional<Delivery> deliveryOptional = deliveryRepository.findById(id);
        return deliveryOptional.orElse(null);
    }

    private boolean isNotCancelable(Delivery delivery) {
        return !((delivery.getOrderStatus().equals(OrderStatus.RECEIVED)
                || delivery.getOrderStatus().equals(OrderStatus.DELIVERING)));
    }
}
