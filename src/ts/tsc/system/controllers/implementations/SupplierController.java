package ts.tsc.system.controllers.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.controllers.interfaces.SupplierControllerInterface;
import ts.tsc.system.controllers.status.Status;
import ts.tsc.system.entities.*;
import ts.tsc.system.entities.keys.DeliveryProductPrimaryKey;
import ts.tsc.system.entities.keys.ShopStorageProductPrimaryKey;
import ts.tsc.system.entities.keys.SupplierStorageProductPrimaryKey;
import ts.tsc.system.repositories.*;
import ts.tsc.system.services.interfaces.BaseService;
import ts.tsc.system.services.interfaces.NamedService;
import ts.tsc.system.services.interfaces.StorageService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/supplier")
public class SupplierController implements SupplierControllerInterface {

    final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @PersistenceContext
    EntityManager entityManager;

    private final SupplierRepository supplierRepository;
    private final NamedService<Supplier, Long> supplierService;
    private final BaseService<Delivery, Long> deliveryService;
    private final ShopStorageRepository shopStorageRepository;
    private final StorageService<Supplier, SupplierStorage, Long> storageService;
    private final SupplierStorageRepository supplierStorageRepository;
    private final SupplierStorageProductRepository supplierStorageProductRepository;
    private final BaseService<SupplierStorageProduct, SupplierStorageProductPrimaryKey> productService;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryProductRepository deliveryProductRepository;
    private final ProductRepository productRepository;
    private final ShopStorageProductRepository shopStorageProductRepository;

    @Autowired
    public SupplierController(SupplierRepository supplierRepository,
                              @Qualifier(value = "baseService") BaseService<Delivery, Long> deliveryService,
                              ShopStorageRepository shopStorageRepository,
                              StorageService<Supplier, SupplierStorage, Long> storageService,
                              SupplierStorageRepository supplierStorageRepository,
                              NamedService<Supplier, Long> supplierService,
                              SupplierStorageProductRepository supplierStorageProductRepository,
                              @Qualifier(value = "baseService") BaseService<SupplierStorageProduct,
                                      SupplierStorageProductPrimaryKey> productService,
                              DeliveryRepository deliveryRepository,
                              DeliveryProductRepository deliveryProductRepository, ProductRepository productRepository, ShopStorageProductRepository shopStorageProductRepository) {
        this.supplierRepository = supplierRepository;
        this.deliveryService = deliveryService;
        this.shopStorageRepository = shopStorageRepository;
        this.storageService = storageService;
        this.supplierStorageRepository = supplierStorageRepository;
        this.supplierService = supplierService;
        this.supplierStorageProductRepository = supplierStorageProductRepository;
        this.productService = productService;
        this.deliveryRepository = deliveryRepository;
        this.deliveryProductRepository = deliveryProductRepository;
        this.productRepository = productRepository;
        this.shopStorageProductRepository = shopStorageProductRepository;
    }

    @Override
    @GetMapping(value = "/list")
    public ResponseEntity<List<Supplier>> findAll() {
        return supplierService.findAll(supplierRepository);
    }

    @Override
    @GetMapping(value = "/name/{name}")
    public ResponseEntity<List<Supplier>> findByName(@PathVariable String name) {
        return supplierService.findByName(name, supplierRepository);
    }

    @Override
    @GetMapping(value = "/{id}")
    public ResponseEntity<Supplier> findById(@PathVariable Long id) {
        return supplierService.findById(id, supplierRepository);
    }

    @Override
    @PostMapping(value = "/")
    public ResponseEntity<?> create(@RequestBody Supplier supplier) {
        return supplierService.save(supplier, supplierRepository);
    }


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

    @Override
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return supplierService.delete(id, supplierRepository);
    }

    @Override
    @GetMapping(value = "/storage/{id}")
    public ResponseEntity<List<SupplierStorage>> findStorageById(@PathVariable Long id) {
        String stringQuery = "select entity from SupplierStorage entity where entity.supplier.id = ?1";
        return storageService.findById(id, stringQuery, supplierStorageRepository);
    }

    @Override
    @GetMapping(value = "/storage/list")
    public ResponseEntity<List<SupplierStorage>> findAllStorages() {
        return storageService.findAll(supplierStorageRepository);
    }

    @Override
    @PostMapping(value = "/storage/{id}")
    public ResponseEntity<?> addStorage(@PathVariable Long id, @RequestBody SupplierStorage storage) {
        return storageService.addStorage(id, storage, supplierRepository, supplierStorageRepository);
    }

    @Override
    @GetMapping(value = "/storage/product/list")
    public ResponseEntity<List<SupplierStorageProduct>> getProducts() {
        return productService.findAll(supplierStorageProductRepository);
    }

    @Override
    @PostMapping(value = "/delivery/{supplierID}/{shopID}/{productIdList}/{countList}")
    public ResponseEntity<?> receiveDelivery(@PathVariable Long supplierID,
                             @PathVariable Long shopID,
                             @PathVariable List<Long> productIdList,
                             @PathVariable List<Integer> countList) {

        if(!(productIdList.size() > 0
                && productIdList.size() == countList.size())) {
            return new ResponseEntity<>("Неверное количество параметров", HttpStatus.BAD_REQUEST);
        }

        Optional<SupplierStorage> supplierStorageOptional
                = supplierStorageRepository.findById(supplierID);
        Optional<ShopStorage> shopStorageOptional
                = shopStorageRepository.findById(shopID);

        int success = 0;

        if(supplierStorageOptional.isPresent() && shopStorageOptional.isPresent()) {
            SupplierStorage supplierStorage = supplierStorageOptional.get();
            ShopStorage shopStorage = shopStorageOptional.get();

            Delivery delivery = new Delivery();
            delivery.setStatus(Status.RECEIVED);
            delivery.setShopStorage(shopStorage);
            delivery.setSupplierStorage(supplierStorage);
            deliveryRepository.save(delivery);

            for(int requestIterator = 0; requestIterator < productIdList.size(); requestIterator++) {
                Long productID = productIdList.get(requestIterator);
                int count = countList.get(requestIterator);
                Optional<Product> productOptional
                        = productRepository.findById(productID);
                if(productOptional.isPresent()) {
                    Product product = productOptional.get();

                    try {
                        TypedQuery<SupplierStorageProduct> productTypedQuery = entityManager.createQuery(
                                "select p from SupplierStorageProduct p " +
                                        "where  p.primaryKey.storage.id = ?1 " +
                                        "and p.primaryKey.product.id = ?2 ",
                                SupplierStorageProduct.class)
                                .setParameter(1, supplierID).setParameter(2, productID);

                        SupplierStorageProduct supplierStorageProduct
                                = productTypedQuery.getSingleResult();

                        if (supplierStorageProduct.getCount() > 0) {
                            if (supplierStorageProduct.getCount() < count) {
                                count = supplierStorageProduct.getCount();
                            }

                            DeliveryProduct deliveryProduct = new DeliveryProduct();
                            deliveryProduct
                                    .setPrimaryKey(new DeliveryProductPrimaryKey(delivery, product));
                            deliveryProduct.setCount(count);
                            BigDecimal sum =
                                    supplierStorageProduct.getPrice().multiply(new BigDecimal(count));
                            deliveryProduct.setSumPrice(sum);
                            deliveryProduct.setPrice(supplierStorageProduct.getPrice());

                            try {
                                deliveryProductRepository.save(deliveryProduct);
                                success++;
                            } catch (Exception e) {
                                logger.error("Ошибка в ходе сохранения элемента заказа", e);
                            }
                        }

                    } catch (Exception e) {
                        logger.error("Ошибка в ходе запроса", e);
                    }
                }
            }
            if(success > 0) {
                deliveryRepository.save(delivery);
                return new ResponseEntity<>(delivery, HttpStatus.OK);
            } else {
                deliveryRepository.delete(delivery);
                return new ResponseEntity<>("Не удалось найти запрошенные товары на складе",
                        HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Не удалось найти запрошенные данные",
                    HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/delivery/status/{id}/{status}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @PathVariable Status status) {
        if(status.equals(Status.DELIVERING)) {
            return transferDelivery(id);
        }
        if(status.equals(Status.COMPLETED)) {
            return completeDelivery(id);
        }
        if(status.equals(Status.CANCELED)) {
            return cancelDelivery(id);
        }
        return new ResponseEntity<>("Unknown status", HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/delivery/list")
    public ResponseEntity<List<Delivery>> getDeliveries() {
        return deliveryService.findAll(deliveryRepository);
    }

    @Override
    public ResponseEntity<?> transferDelivery(Long id) {
        Optional<Delivery> deliveryOptional = deliveryRepository.findById(id);
        if(deliveryOptional.isPresent()) {
            Delivery delivery = deliveryOptional.get();

            if(!delivery.getStatus().equals(Status.RECEIVED)) {
                return new ResponseEntity<>("Предыдущее состояние заказа должно быть RECEIVED " +
                        "для перевода в DELIVERING",
                        HttpStatus.BAD_REQUEST);
            }
            delivery.setStatus(Status.DELIVERING);
            deliveryRepository.save(delivery);
            int success = 0;

            Optional<SupplierStorage> supplierStorageOptional
                    = supplierStorageRepository.findById(delivery.getSupplierStorage().getId());

            if(supplierStorageOptional.isPresent()) {
                SupplierStorage supplierStorage = supplierStorageOptional.get();
                try {
                    TypedQuery<DeliveryProduct> deliveryProductQuery =
                            entityManager.createQuery(
                                    "select p from DeliveryProduct p " +
                                            "where p.primaryKey.delivery.id = ?1",
                                    DeliveryProduct.class)
                                    .setParameter(1, delivery.getId());

                    List<DeliveryProduct> deliveryProductList = deliveryProductQuery.getResultList();

                    for(DeliveryProduct deliveryProduct : deliveryProductList) {
                        try {
                            TypedQuery<SupplierStorageProduct> supplierStorageProductTypedQuery =
                                    entityManager.createQuery(
                                            "select p from SupplierStorageProduct p " +
                                                    "where p.primaryKey.storage.id = ?1 " +
                                                    "and p.primaryKey.product.id = ?2",
                                            SupplierStorageProduct.class)
                                            .setParameter(1, supplierStorage.getId())
                                            .setParameter(2, deliveryProduct
                                                    .getPrimaryKey().getProduct().getId());

                            SupplierStorageProduct supplierStorageProduct
                                    = supplierStorageProductTypedQuery.getSingleResult();

                            supplierStorageProduct.setCount(supplierStorageProduct.getCount()
                                    -deliveryProduct.getCount());
                            supplierStorageProductRepository.save(supplierStorageProduct);
                            supplierStorage.setFreeSpace(supplierStorage.getFreeSpace()
                                    +deliveryProduct.getCount());

                            supplierStorageRepository.save(supplierStorage);

                            if(supplierStorageProduct.getCount() == 0) {
                                supplierStorageProductRepository.delete(supplierStorageProduct);
                            } else {
                                supplierStorageProductRepository.save(supplierStorageProduct);
                            }

                            success++;

                        } catch (Exception e){}//todo;
                    }
                    if(success == delivery.getDeliveryProducts().size()) {
                        return new ResponseEntity<>(delivery, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("Не удалось провести транзакцию",
                                HttpStatus.BAD_REQUEST);
                    }
                } catch (Exception e) {
                    return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> completeDelivery(Long id) {
        Optional<Delivery> deliveryOptional = deliveryRepository.findById(id);

        if(!deliveryOptional.isPresent()) {
            return new ResponseEntity<>("Не удалось найти заказ с указанным идентификатором",
                    HttpStatus.NOT_FOUND);
        }

        Delivery delivery = deliveryOptional.get();

        if(!delivery.getStatus().equals(Status.DELIVERING)) {
            return new ResponseEntity<>("Предыдущее состояние заказа должно быть DELIVERING " +
                    "для перевода в COMPLETED",
                    HttpStatus.NOT_FOUND);
        }

        delivery.setStatus(Status.COMPLETED);
        ShopStorage shopStorage = delivery.getShopStorage();

        List<DeliveryProduct> deliveryProductList;
        try {
            TypedQuery<DeliveryProduct> deliveryProductQuery =
                    entityManager.createQuery(
                            "select p from DeliveryProduct p " +
                                    "where p.primaryKey.delivery.id = ?1",
                            DeliveryProduct.class)
                            .setParameter(1, delivery.getId());

            deliveryProductList = deliveryProductQuery.getResultList();
        } catch (Exception e) {
            logger.error("Ошибка в ходе запроса списка продуктов", e);
            return new ResponseEntity<>("Ошибка в ходе запроса списка продуктов",
                    HttpStatus.BAD_REQUEST);
        }

        for(DeliveryProduct deliveryProduct : deliveryProductList) {

            ShopStorageProductPrimaryKey primaryKey =
                    new ShopStorageProductPrimaryKey(shopStorage,
                            deliveryProduct.getPrimaryKey().getProduct());
            Optional<ShopStorageProduct> shopStorageProductOptional
                    = shopStorageProductRepository.findById(primaryKey);

            ShopStorageProduct shopStorageProduct;
            if(shopStorageProductOptional.isPresent()) {
                shopStorageProduct = shopStorageProductOptional.get();
            } else  {
                shopStorageProduct  = new ShopStorageProduct();
                shopStorageProduct.setPrimaryKey(primaryKey);
                shopStorageProduct.setCount(deliveryProduct.getCount());
                shopStorageProduct.setPrice(deliveryProduct.getPrice());
            }
            shopStorageProductRepository.save(shopStorageProduct);
        }

        deliveryRepository.save(delivery);
        return new ResponseEntity<>(delivery, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> cancelDelivery(Long id) {

        Optional<Delivery> deliveryOptional = deliveryRepository.findById(id);
        if(!deliveryOptional.isPresent()) {
            return new ResponseEntity<>("Заказ с указанным идентификатором не найден",
                    HttpStatus.NOT_FOUND);
        }

        Delivery delivery = deliveryOptional.get();
        List<SupplierStorageProduct> supplierStorageProductList = new LinkedList<>();

        if(!(delivery.getStatus().equals(Status.RECEIVED)
                || delivery.getStatus().equals(Status.DELIVERING))) {
            return new ResponseEntity<>("Невозможно поменять статус заказа (он уже выполнен или отменен)",
                    HttpStatus.BAD_REQUEST);
        }
        Status oldStatus = delivery.getStatus();
        delivery.setStatus(Status.CANCELED);

        if(oldStatus.equals(Status.RECEIVED)) {
            try {
                deliveryRepository.save(delivery);
            } catch (Exception e) {
                return new ResponseEntity<>("Не удалось отменить заказ",
                        HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(delivery);
        }

        Optional<SupplierStorage> supplierStorageOptional
                = supplierStorageRepository.findById(delivery.getSupplierStorage().getId());
        if (!supplierStorageOptional.isPresent()) {
            return new ResponseEntity<>
                    ("Не найден склад с указанным в заказе идентификатором",
                            HttpStatus.NOT_FOUND);
        }

        SupplierStorage supplierStorage = supplierStorageOptional.get();

        for(DeliveryProduct deliveryProduct : delivery.getDeliveryProducts()) {
            try {
                TypedQuery<SupplierStorageProduct> supplierStorageProductTypedQuery =
                        entityManager.createQuery(
                                "select p from SupplierStorageProduct p " +
                                        "where p.primaryKey.storage.id = ?1 " +
                                        "and p.primaryKey.product.id = ?2",
                                SupplierStorageProduct.class)
                                .setParameter(1, delivery.getSupplierStorage().getId())
                                .setParameter(2, deliveryProduct.getPrimaryKey().getProduct().getId());
                SupplierStorageProduct supplierStorageProduct
                        = supplierStorageProductTypedQuery.getSingleResult();

                supplierStorageProduct.setCount(supplierStorageProduct.getCount()+deliveryProduct.getCount());
                supplierStorageProductList.add(supplierStorageProduct);
                setFreeSpace(deliveryProduct, supplierStorage);

            } catch (Exception e) {
                SupplierStorageProduct supplierStorageProduct = new SupplierStorageProduct();
                supplierStorageProduct
                        .setPrimaryKey(new SupplierStorageProductPrimaryKey(delivery.getSupplierStorage(),
                                deliveryProduct.getPrimaryKey().getProduct()));
                supplierStorageProduct.setCount(deliveryProduct.getCount());
                supplierStorageProduct.setPrice(deliveryProduct.getPrice());
                supplierStorageProductList.add(supplierStorageProduct);

                setFreeSpace(deliveryProduct, supplierStorage);
            }

        }

        if(supplierStorage.getFreeSpace() > supplierStorage.getTotalSpace()) {
            return new ResponseEntity<>("На складе нет места для товаров", HttpStatus.BAD_REQUEST);
        }

        try {
            deliveryRepository.save(delivery);
            supplierStorageRepository.save(supplierStorage);
            for(SupplierStorageProduct supplierStorageProduct : supplierStorageProductList) {
                supplierStorageProductRepository.save(supplierStorageProduct);
            }
        }
        catch (Exception e) {
            return new ResponseEntity<>("Ошибка в ходе сохранения данных",
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(delivery, HttpStatus.OK);
    }


    private void setFreeSpace(DeliveryProduct deliveryProduct, SupplierStorage supplierStorage) {
        int count = deliveryProduct.getCount();
        int freeSpace = supplierStorage.getFreeSpace();
        supplierStorage.setFreeSpace(freeSpace-count);
    }

    @GetMapping(value = "/string/{name}")
    public ResponseEntity<?> getName(@PathVariable List<String> name) {
        String result ="";
        for(String s : name) {
            result+=s + " ";
        }
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }
}