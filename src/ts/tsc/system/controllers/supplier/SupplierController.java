package ts.tsc.system.controllers.supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.controllers.parent.ExtendedControllerInterface;
import ts.tsc.system.controllers.parent.OrderController;
import ts.tsc.system.controllers.parent.SupplierOrderInterface;
import ts.tsc.system.controllers.status.enums.ErrorStatus;
import ts.tsc.system.controllers.status.enums.Status;
import ts.tsc.system.entity.delivery.Delivery;
import ts.tsc.system.entity.delivery.DeliveryProduct;
import ts.tsc.system.entity.delivery.DeliveryProductPrimaryKey;
import ts.tsc.system.entity.parent.BaseStorage;
import ts.tsc.system.entity.shop.*;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.entity.supplier.SupplierStorage;
import ts.tsc.system.entity.supplier.SupplierStorageProduct;
import ts.tsc.system.entity.supplier.SupplierStorageProductPrimaryKey;
import ts.tsc.system.repository.*;
import ts.tsc.system.service.base.BaseService;
import ts.tsc.system.service.named.NamedService;
import ts.tsc.system.service.storage.StorageService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/supplier")
public class SupplierController
        extends OrderController
        implements SupplierOrderInterface,
        ExtendedControllerInterface<Supplier, SupplierStorage> {

    private final Logger logger = LoggerFactory.getLogger(SupplierController.class);

    @PersistenceContext
    EntityManager entityManager;

    private final SupplierRepository supplierRepository;
    private final ShopRepository shopRepository;
    private final NamedService<Supplier, Long> supplierService;
    private final BaseService<Delivery, Long> deliveryService;
    private final ShopStorageRepository shopStorageRepository;
    private final StorageService<Supplier, SupplierStorage, Long> storageService;
    private final SupplierStorageRepository supplierStorageRepository;
    private final SupplierStorageProductRepository supplierStorageProductRepository;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryProductRepository deliveryProductRepository;
    private final ShopStorageProductRepository shopStorageProductRepository;

    @Autowired
    public SupplierController(SupplierRepository supplierRepository,
                              ShopRepository shopRepository,
                              @Qualifier(value = "baseService")
                                          BaseService<Delivery, Long> deliveryService,
                              ShopStorageRepository shopStorageRepository,
                              StorageService<Supplier, SupplierStorage, Long> storageService,
                              SupplierStorageRepository supplierStorageRepository,
                              NamedService<Supplier, Long> supplierService,
                              SupplierStorageProductRepository supplierStorageProductRepository,
                              DeliveryRepository deliveryRepository,
                              DeliveryProductRepository deliveryProductRepository,
                              ShopStorageProductRepository shopStorageProductRepository) {
        this.supplierRepository = supplierRepository;
        this.shopRepository = shopRepository;
        this.deliveryService = deliveryService;
        this.shopStorageRepository = shopStorageRepository;
        this.storageService = storageService;
        this.supplierStorageRepository = supplierStorageRepository;
        this.supplierService = supplierService;
        this.supplierStorageProductRepository = supplierStorageProductRepository;
        this.deliveryRepository = deliveryRepository;
        this.deliveryProductRepository = deliveryProductRepository;
        this.shopStorageProductRepository = shopStorageProductRepository;
    }

    /**
     * Поиск всех магазинов
     * @return {@link ts.tsc.system.service.base.BaseServiceImplementation#findAll(JpaRepository)}
     */
    @Override
    @GetMapping(value = "/list")
    public ResponseEntity<?> findAll() {
        return supplierService.findAll(supplierRepository);
    }

    /**
     * Поиск по названию магазина
     * @param name название, по которому будет происходить поиск
     * @return {@link ts.tsc.system.service.named.NamedServiceImplementation#findByName(String, NamedRepository)}
     */
    @Override
    @GetMapping(value = "/name/{name}")
    public ResponseEntity<?> findByName(@PathVariable String name) {
        return supplierService.findByName(name, supplierRepository);
    }

    /**
     * Поиск магазина по идентификатору
     * @param id идентификатор запрашиваемого объекта
     * @return {@link ts.tsc.system.service.base.BaseServiceImplementation#findById(Object, JpaRepository)}
     */
    @Override
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return supplierService.findById(id, supplierRepository);
    }

    /**
     * Добавление нового магазина
     * @param supplier объект типа Supplier
     * @return {@link ts.tsc.system.service.base.BaseServiceImplementation#save(Object, JpaRepository)}
     */
    @Override
    @PostMapping(value = "/")
    public ResponseEntity<?> create(@RequestBody Supplier supplier) {
        return supplierService.save(supplier, supplierRepository);
    }

    /**
     * Обновление данных магазина
     * @param id идентификатор искомого магазина
     * @param supplier объект
     * @return объект и код 200, если удалось обновить, иначе код 422
     */
    @Override
    @PutMapping(value = "/{id}")
    public ResponseEntity<Supplier> update(@PathVariable Long id, @RequestBody Supplier supplier) {
        return supplierRepository.findById(id)
                .map(record -> {
                    record.setName(supplier.getName());
                    Supplier updated = supplierRepository.save(record);
                    return ResponseEntity.ok().body(updated);
                }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Поиск склада по идентификтаору
     * @param id идентификтаор склада
     * @return {@link ts.tsc.system.service.storage.StorageServiceImplementation#findById(Object, JpaRepository)}
     */
    @Override
    @GetMapping(value = "/storage/{id}")
    public ResponseEntity<List<SupplierStorage>> findStorageById(@PathVariable Long id) {
        String stringQuery = "select entity from SupplierStorage entity where entity.id = ?1";
        return storageService.findById(id, stringQuery, supplierStorageRepository);
    }

    /**
     * Поиск всех складов магазинов
     * @return {@link ts.tsc.system.service.base.BaseServiceImplementation#findAll(JpaRepository)}
     */
    @Override
    @GetMapping(value = "/storage/list")
    public ResponseEntity<?> findAllStorage() {
        return storageService.findAll(supplierStorageRepository);
    }

    /**
     * Поиск складов по идентификтору магазина
     * @param id идентификтаор магазина
     * @return {@link ts.tsc.system.service.storage.StorageServiceImplementation#findById(Long, String, JpaRepository)}
     */
    @Override
    @GetMapping(value = "/storage/list/{id}")
    public ResponseEntity<?> findStorageByOwnerId(@PathVariable Long id) {
        String stringQuery = "select entity from SupplierStorage entity where entity.supplier.id = ?1";
        return storageService.findById(id, stringQuery, supplierStorageRepository);
    }

    /**
     * Поиск заказа по идентификатору
     * @param id идентификатор заказа
     * @return {@link ts.tsc.system.service.base.BaseServiceImplementation#findById(Object, JpaRepository)}
     */
    @Override
    @GetMapping(value = "/order/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        return deliveryService.findById(id, deliveryRepository);
    }

    /**
     * Добавление склада магазину
     * @param id идентификатор магазина
     * @param storage объект типа Storage, который будет добавлен
     * @return {@link ts.tsc.system.service.storage.StorageServiceImplementation#addStorage(Object, BaseStorage, JpaRepository, JpaRepository)}
     */
    @Override
    @PostMapping(value = "/storage/{id}")
    public ResponseEntity<?> addStorage(@PathVariable Long id, @RequestBody SupplierStorage storage) {
        return storageService.addStorage(id, storage, supplierRepository, supplierStorageRepository);
    }


    /**
     * Получение списка продуктов со склада
     * @return {@link OrderController#getStorageProducts(Long, JpaRepository)}
     */
    @Override
    @SuppressWarnings("unchecked")
    @GetMapping(value = "/storage/product/list/{id}")
    public ResponseEntity<?> getStorageProducts(@PathVariable Long id) {
        return getStorageProducts(id, supplierStorageRepository);
    }

    /**
     * Обработка поступившего заказа
     * @param supplierID идентификатор склада поставщика
     * @param shopStorageID идентификатор склада магазина
     * @param productIdList список идентификаторов товаров
     * @param countList список количества товаров
     * @return
     */
    @Override
    @PostMapping(value = "/order/{supplierID}/{shopStorageID}/{productIdList}/{countList}")
    public ResponseEntity<?> receiveOrder(@PathVariable Long supplierID,
                             @PathVariable Long shopStorageID,
                             @PathVariable List<Long> productIdList,
                             @PathVariable List<Integer> countList) {

        if(productIdList.size() != countList.size()) {
            return new ResponseEntity<>(ErrorStatus.WRONG_NUMBER_OF_PARAMETERS
                    +  " of products and counts",
                    HttpStatus.BAD_REQUEST);
        }

        Optional<SupplierStorage> supplierStorageOptional
                = supplierStorageRepository.findById(supplierID);
        if(!supplierStorageOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + " supplierStorage",
                    HttpStatus.NOT_FOUND);
        }

        Optional<ShopStorage> shopStorageOptional
                = shopStorageRepository.findById(shopStorageID);
        if(!shopStorageOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + " shopStorage",
                    HttpStatus.NOT_FOUND);
        }

        SupplierStorage supplierStorage = supplierStorageOptional.get();
        ShopStorage shopStorage = shopStorageOptional.get();

        int productsSumCount = countList.stream().reduce(0, Integer::sum);

        if(shopStorage.getFreeSpace() < productsSumCount) {
            return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_SPACE + " in shopStorage",
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
                    return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_PRODUCTS + " supplier storage",
                            HttpStatus.BAD_REQUEST);
                }
            } catch (Exception e) {
                logger.error("Error: ", e);
                return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + " sum of product",
                        HttpStatus.NOT_FOUND);
            }
        }

        BigDecimal sumPrice = new BigDecimal(0);

        for(int deliveryIterator = 0; deliveryIterator < productIdList.size(); deliveryIterator++) {
            Long productID = productIdList.get(deliveryIterator);
            Integer count = countList.get(deliveryIterator);
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
                return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + " sum of product",
                        HttpStatus.NOT_FOUND);
            }
        }

        Optional<Shop> shopOptional = shopRepository.findById(shopStorage.getShop().getId());
        if(!shopOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + " shop",
                    HttpStatus.NOT_FOUND);
        }

        Shop shop = shopOptional.get();

        if(sumPrice.compareTo(shop.getBudget()) > 0) {
            return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_MONEY + " in shop",
                    HttpStatus.BAD_REQUEST);
        }

        Delivery delivery = new Delivery();
        delivery.setStatus(Status.RECEIVED);
        delivery.setShopStorage(shopStorage);
        delivery.setSupplierStorage(supplierStorage);

        try {
            deliveryRepository.save(delivery);
        } catch (Exception e) {
            logger.error("Error: ", e);
            new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + " delivery",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return transfer(productIdList, countList, supplierStorage, delivery, shop);
    }

    /**
     * Оформление заказа RECEIVED или отмена CANCEL
     * @param productIdList список идентификаторов продуктов
     * @param countList список количества товаров
     * @param supplierStorage объект кслада поставщика
     * @param delivery объект заказа
     * @param shop объект магазина
     * @return
     */
    private ResponseEntity<?> transfer(List<Long> productIdList,
                          List<Integer> countList,
                          SupplierStorage supplierStorage,
                          Delivery delivery, Shop shop) {
        for(int deliveryIterator = 0; deliveryIterator < productIdList.size(); deliveryIterator++) {
            Long productID = productIdList.get(deliveryIterator);
            Integer count = countList.get(deliveryIterator);
            try {
                TypedQuery<SupplierStorageProduct> sumCountTypedQuery = entityManager.createQuery(
                        "select p from SupplierStorageProduct p " +
                                "where p.primaryKey.storage.id = ?1 " +
                                "and p.primaryKey.product.id = ?2",
                        SupplierStorageProduct.class)
                        .setParameter(1, supplierStorage.getId())
                        .setParameter(2, productID);
                SupplierStorageProduct supplierStorageProduct = sumCountTypedQuery.getSingleResult();

                if(delivery.getStatus().equals(Status.RECEIVED)) {
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
                        return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING,
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }

                int coefficient = -1;
                if(delivery.getStatus().equals(Status.RECEIVED)) {
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
                    return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING,
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }

            } catch (Exception e) {
                logger.error("Error: ", e);
                return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND,
                        HttpStatus.NOT_FOUND);
            }
        }
        try {
            supplierStorageRepository.save(supplierStorage);
            shopRepository.save(shop);
        } catch (Exception e) {
            logger.error("Error: ", e);
            new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(delivery, HttpStatus.OK);
    }

    /**
     * Изменение состояние заказа
     * @param id идентификатор заказа
     * @param status новое состояние
     * @return {@link OrderController#changeStatus(Long, Status)}
     */
    @PutMapping(value = "/order/status/{id}/{status}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @PathVariable Status status) {
        return super.changeStatus(id, status);
    }

    /**
     * Поиск всех доставок
     * @return {@link ts.tsc.system.service.base.BaseService#findAll(JpaRepository)}
     */
    @GetMapping(value = "/order/list")
    public ResponseEntity<?> getAllOrders() {
        return deliveryService.findAll(deliveryRepository);
    }

    /**
     * Перевод заказа в состояние DELIVERING
     * @param id идентификатор заказа
     * @return
     */
    @Override
    protected ResponseEntity<?> deliverOrder(Long id) {
        Delivery delivery = isExist(id);
        if(delivery == null) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND,
                    HttpStatus.NOT_FOUND);
        }

        if(!delivery.getStatus().equals(Status.RECEIVED)) {
            return new ResponseEntity<>(ErrorStatus.WRONG_DELIVERY_STATUS, HttpStatus.BAD_REQUEST);
        }

        delivery.setStatus(Status.DELIVERING);

        try {
            deliveryRepository.save(delivery);
        } catch (Exception e) {
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + " delivery",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(delivery, HttpStatus.OK);
    }

    /**
     * Завершение заказа
     * @param id идентификатор заказа
     * @return
     */
    @Override
    protected ResponseEntity<?> completeOrder(Long id) {
        Optional<Delivery> deliveryOptional = deliveryRepository.findById(id);

        if(!deliveryOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        Delivery delivery = deliveryOptional.get();

        if(!delivery.getStatus().equals(Status.DELIVERING)) {
            return new ResponseEntity<>(ErrorStatus.WRONG_DELIVERY_STATUS, HttpStatus.BAD_REQUEST);
        }

        delivery.setStatus(Status.COMPLETED);
        ShopStorage shopStorage = delivery.getShopStorage();
        int sumProductCount = 0;

        try {
            TypedQuery<Integer> sumProductTypedQuery = entityManager.createQuery(
                    "select  sum(p.count) from DeliveryProduct " +
                            "where p.primaryKey.delivery.id = ?1",
                    Integer.class).setParameter(1, delivery.getId());
            sumProductCount = sumProductTypedQuery.getSingleResult();
        } catch (Exception e) {
            logger.error("Error", e);
            new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        if(shopStorage.getFreeSpace() < sumProductCount) {
            return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_SPACE + " in shopStorage",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            TypedQuery<DeliveryProduct> deliveryProductQuery =
                    entityManager.createQuery(
                            "select p from DeliveryProduct p " +
                                    "where p.primaryKey.delivery.id = ?1",
                            DeliveryProduct.class)
                            .setParameter(1, delivery.getId());

            List<DeliveryProduct> deliveryProductList = deliveryProductQuery.getResultList();

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
                    shopStorageRepository.save(shopStorage);
                } catch (Exception e) {
                    logger.error("Error", e);
                    new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING,
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (Exception e) {
            logger.error("Error", e);
            new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        try {
            deliveryRepository.save(delivery);
        } catch (Exception e) {
            logger.error("Error", e);
            new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(delivery, HttpStatus.OK);
    }

    /**
     * Отмена заказа
     * @param id идентификатор заказа
     * @return
     */
    @Override
    protected ResponseEntity<?> cancelOrder(Long id) {
        Delivery delivery = isExist(id);
        if(delivery == null) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        if(isNotCancelable(delivery)) {
            return new ResponseEntity<>(ErrorStatus.CAN_NOT_BE_CANCELED,
                    HttpStatus.BAD_REQUEST);
        }
        delivery.setStatus(Status.CANCELED);


        Optional<SupplierStorage> supplierStorageOptional
                = supplierStorageRepository.findById(delivery.getSupplierStorage().getId());
        if(!supplierStorageOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + " supplierStorage",
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
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + " sum of product",
                    HttpStatus.NOT_FOUND);
        }

        int productsSumCount = countList.stream().reduce(0, Integer::sum);

        SupplierStorage supplierStorage = supplierStorageOptional.get();
        if(supplierStorage.getFreeSpace() < productsSumCount) {
            return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_SPACE + " in supplierStorageProduct",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            deliveryRepository.save(delivery);
        } catch (Exception e) {
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + " delivery",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Optional<Shop> shopOptional
                = shopRepository.findById(delivery.getShopStorage().getShop().getId());
        if(!shopOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + " shop",
                    HttpStatus.NOT_FOUND);
        }
        Shop shop = shopOptional.get();

        return transfer(productIdList, countList, supplierStorage, delivery, shop);
    }

    private Delivery isExist(Long id) {
        Optional<Delivery> deliveryOptional = deliveryRepository.findById(id);
        return deliveryOptional.orElse(null);
    }
}