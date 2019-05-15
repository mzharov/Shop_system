package ts.tsc.system.controllers.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.controllers.interfaces.ExtendedControllerInterface;
import ts.tsc.system.controllers.interfaces.ShopControllerDeliveryInterface;
import ts.tsc.system.controllers.status.enums.ErrorStatus;
import ts.tsc.system.controllers.status.enums.Status;
import ts.tsc.system.entities.*;
import ts.tsc.system.entities.keys.PurchaseProductPrimaryKey;
import ts.tsc.system.entities.keys.ShopStorageProductPrimaryKey;
import ts.tsc.system.repositories.*;
import ts.tsc.system.services.interfaces.BaseService;
import ts.tsc.system.services.interfaces.NamedService;
import ts.tsc.system.services.interfaces.StorageService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/shop")
public class ShopController implements
        ShopControllerDeliveryInterface,
        ExtendedControllerInterface<Shop, ShopStorage, ShopStorageProduct> {

    final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @PersistenceContext
    EntityManager entityManager;

    private final ShopRepository shopRepository;
    private final NamedService<Shop, Long> shopService;
    private final StorageService<Shop, ShopStorage, Long> storageService;
    private final ShopStorageRepository shopStorageRepository;
    private final ShopStorageProductRepository shopStorageProductRepository;
    private final BaseService<ShopStorageProduct, ShopStorageProductPrimaryKey> productService;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseProductRepository purchaseProductRepository;
    private final BaseService<Purchase, Long> purchaseService;

    @Autowired
    public ShopController(ShopRepository shopRepository,
                          NamedService<Shop, Long> shopService,
                          StorageService<Shop, ShopStorage, Long> storageService,
                          ShopStorageRepository shopStorageRepository,
                          ShopStorageProductRepository shopStorageProductRepository,
                          @Qualifier(value = "baseService") BaseService<ShopStorageProduct, ShopStorageProductPrimaryKey> productService,
                          PurchaseRepository purchaseRepository,
                          PurchaseProductRepository purchaseProductRepository,
                          @Qualifier(value = "baseService") BaseService<Purchase, Long> purchaseService) {
        this.shopRepository = shopRepository;
        this.shopService = shopService;
        this.storageService = storageService;
        this.shopStorageRepository = shopStorageRepository;
        this.shopStorageProductRepository = shopStorageProductRepository;
        this.productService = productService;
        this.purchaseRepository = purchaseRepository;
        this.purchaseProductRepository = purchaseProductRepository;
        this.purchaseService = purchaseService;
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<Shop>> findAll() {
        return shopService.findAll(shopRepository);
    }

    @GetMapping(value = "/name/{name}")
    public ResponseEntity<List<Shop>> findByName(@PathVariable String name) {
        return shopService.findByName(name, shopRepository);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Shop> findById(@PathVariable Long id) {
        return shopService.findById(id, shopRepository);
    }


    @PostMapping(value = "/")
    public ResponseEntity<?> create(@RequestBody Shop shop) {
        return shopService.save(shop, shopRepository);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Shop> update(@PathVariable Long id, @RequestBody Shop shop) {
        return shopRepository.findById(id)
                .map(record -> {
                    record.setName(shop.getName());
                    record.setBudget(shop.getBudget());
                    Shop updated = shopRepository.save(record);
                    return ResponseEntity.ok().body(updated);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return shopService.delete(id, shopRepository);
    }

    @GetMapping(value = "/storage/{id}")
    public ResponseEntity<List<ShopStorage>> findStorageById(@PathVariable Long id) {
        String stringQuery = "select entity from ShopStorage entity where entity.id = ?1";
        return storageService.findById(id, stringQuery, shopStorageRepository);
    }


    @GetMapping(value = "/storage/list")
    public ResponseEntity<List<ShopStorage>> findAllStorages() {
        return storageService.findAll(shopStorageRepository);
    }

    @PostMapping(value = "/storage/{id}")
    public ResponseEntity<?> addStorage(@PathVariable Long id, @RequestBody ShopStorage storage) {
        return storageService.addStorage(id, storage, shopRepository, shopStorageRepository);
    }

    @GetMapping(value = "/storage/product/list")
    public ResponseEntity<List<ShopStorageProduct>> getProducts() {
        return productService.findAll(shopStorageProductRepository);
    }

    @GetMapping(value = "/order/list")
    public ResponseEntity<?> getPurchases() {
        return purchaseService.findAll(purchaseRepository);
    }

    @Override
    @PostMapping(value = "/order/{shopID}/{productIdList}/{countList}")
    public ResponseEntity<?> receiveOrder(@PathVariable Long shopID,
                                          @PathVariable List<Long> productIdList,
                                          @PathVariable List<Integer> countList) {

        if(productIdList.size() != countList.size()) {
            return new ResponseEntity<>(ErrorStatus.WRONG_NUMBER_OF_PARAMETERS,
                    HttpStatus.BAD_REQUEST);
        }

        Optional<Shop> shopOptional = shopRepository.findById(shopID);
        if(!shopOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND,
                    HttpStatus.NOT_FOUND);
        }
        Shop shop = shopOptional.get();

        ShopStorage shopStorage;
        try {
            TypedQuery<ShopStorage> shopStorageTypedQuery =
                    entityManager.createQuery(
                            "select p from ShopStorage p " +
                                    "where p.shop.id = ?1 " +
                                    "and p.type = ?2",
                            ShopStorage.class)
                            .setParameter(1, shopID)
                            .setParameter(2, 1);
            shopStorage = shopStorageTypedQuery.getSingleResult();
        } catch (Exception e) {
            logger.error(ErrorStatus.ELEMENT_NOT_FOUND.toString(), e);
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        for(int orderIterator = 0; orderIterator < productIdList.size(); orderIterator++) {
            Long productID = productIdList.get(orderIterator);
            Integer count = countList.get(orderIterator);
            try {
                TypedQuery<Integer> productCountInStorageQuery
                        = entityManager.createQuery(
                        "select p.count from ShopStorageProduct p " +
                                "where p.primaryKey.storage.id = ?1 " +
                                "and p.primaryKey.product.id =?2" ,
                        Integer.class).setParameter(1, shopStorage.getId())
                        .setParameter(2, productID);
                int productCountInStorage = productCountInStorageQuery.getSingleResult();
                if(productCountInStorage < count) {
                    return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_PRODUCTS + " shop storage",
                            HttpStatus.BAD_REQUEST);
                }
            } catch (Exception e) {
                logger.error("Error: ", e);
                return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + " sum of product ",
                        HttpStatus.NOT_FOUND);
            }
        }

        Purchase purchase = new Purchase();
        purchase.setShop(shop);
        purchase.setStatus(Status.RECEIVED);

        try {
            purchaseRepository.save(purchase);
        } catch (Exception e) {
            logger.error("Error: ", e);
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        for(int orderIterator = 0; orderIterator < productIdList.size(); orderIterator++) {
            Long productID = productIdList.get(orderIterator);
            Integer count = countList.get(orderIterator);

            TypedQuery<ShopStorageProduct> shopStorageProductTypedQuery =
                    entityManager.createQuery(
                            "select p from ShopStorageProduct p " +
                                    "where p.primaryKey.storage.id = ?1 " +
                                    "and p.primaryKey.product.id = ?2",
                            ShopStorageProduct.class)
                            .setParameter(1, shopStorage.getId()).setParameter(2, productID);
            ShopStorageProduct shopStorageProduct = shopStorageProductTypedQuery.getSingleResult();

            Product product = shopStorageProduct.getPrimaryKey().getProduct();
            PurchaseProduct purchaseProduct = new PurchaseProduct();
            purchaseProduct.setPrimaryKey(new PurchaseProductPrimaryKey(purchase, product));
            purchaseProduct.setCount(count);
            BigDecimal price = shopStorageProduct.getPrice();
            purchaseProduct.setPrice(price);
            BigDecimal sumPrice = price.multiply(new BigDecimal(count));
            purchaseProduct.setSumPrice(sumPrice);

            shopStorageProduct.setCount(shopStorageProduct.getCount()-count);
            shopStorage.setFreeSpace(shopStorage.getFreeSpace()-count);

            try {
                purchaseProductRepository.save(purchaseProduct);
                shopStorageProductRepository.save(shopStorageProduct);
            } catch (Exception e) {
                logger.error("Error: ", e);
                return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        try {
            shopStorageRepository.save(shopStorage);
        } catch (Exception e) {
            logger.error("Error: ", e);
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(purchase, HttpStatus.OK);
    }

    @PostMapping(value = "/order/status/{id}/{status}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @PathVariable Status status) {
        if(status.equals(Status.DELIVERING)) {
            return deliverOrder(id);
        }
        if(status.equals(Status.COMPLETED)) {
            return completeOrder(id);
        }
        if(status.equals(Status.CANCELED)) {
            return cancelOrder(id);
        }
        return new ResponseEntity<>(ErrorStatus.UNKNOWN_DELIVER_STATUS, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> deliverOrder(Long id) {
        Optional<Purchase> deliveryOptional = purchaseRepository.findById(id);
        if(!deliveryOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + " purchase", HttpStatus.NOT_FOUND);
        }

        Purchase purchase = deliveryOptional.get();
        if(!purchase.getStatus().equals(Status.RECEIVED)) {
            return new ResponseEntity<>(ErrorStatus.WRONG_DELIVERY_STATUS, HttpStatus.BAD_REQUEST);
        }

        purchase.setStatus(Status.DELIVERING);

        try {
            purchaseRepository.save(purchase);
        } catch (Exception e) {
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING + " purchase",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(purchase, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> completeOrder(Long id) {
        Optional<Purchase> purchaseOptional = purchaseRepository.findById(id);
        if(!purchaseOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + " purchase", HttpStatus.NOT_FOUND);
        }
        Purchase purchase = purchaseOptional.get();

        if(!purchase.getStatus().equals(Status.DELIVERING)) {
            return new ResponseEntity<>(ErrorStatus.WRONG_DELIVERY_STATUS, HttpStatus.NOT_FOUND);
        }
        purchase.setStatus(Status.COMPLETED);
        purchaseRepository.save(purchase);
        return new ResponseEntity<>(purchase, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> cancelOrder(Long id) {
        Optional<Purchase> purchaseOptional = purchaseRepository.findById(id);

        if(!purchaseOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        Purchase purchase = purchaseOptional.get();

        if(purchase.getStatus().equals(Status.RECEIVED)) {
            purchase.setStatus(Status.CANCELED);
            purchaseRepository.save(purchase);
            return new ResponseEntity<>(purchase, HttpStatus.OK);
        }

        purchase.setStatus(Status.CANCELED);
        ShopStorage shopStorage;
        try {
            TypedQuery<ShopStorage> shopStorageTypedQuery =
                    entityManager.createQuery(
                            "select p from ShopStorage p " +
                                    "where p.shop.id = ?1 " +
                                    "and p.type = ?2",
                            ShopStorage.class)
                            .setParameter(1, purchase.getShop().getId())
                            .setParameter(2, 1);
            shopStorage = shopStorageTypedQuery.getSingleResult();
        } catch (Exception e) {
            logger.error(ErrorStatus.ELEMENT_NOT_FOUND.toString(), e);
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        List<PurchaseProduct> purchaseProductList = new LinkedList<>(purchase
                .getPurchaseProducts());
        List<ShopStorageProduct> shopStorageProductList = new LinkedList<>();

        for(PurchaseProduct purchaseProduct : purchaseProductList) {
            try {
                TypedQuery<ShopStorageProduct> shopStorageTypedQuery =
                        entityManager.createQuery(
                                "select p from ShopStorageProduct p " +
                                        "where p.primaryKey.storage.id = ?1 " +
                                        "and p.primaryKey.product.id =?2",
                                ShopStorageProduct.class)
                                .setParameter(1, shopStorage.getId())
                                .setParameter(2, purchaseProduct.getPrimaryKey().getProduct().getId());

                ShopStorageProduct shopStorageProduct = shopStorageTypedQuery.getSingleResult();
                int storageCount = shopStorageProduct.getCount();
                int orderCount = purchaseProduct.getCount();

                shopStorageProduct.setCount(storageCount+orderCount);
                shopStorageProductList.add(shopStorageProduct);
                shopStorage.setFreeSpace(shopStorage.getFreeSpace()-orderCount);

                if(shopStorage.getFreeSpace() > shopStorage.getTotalSpace()) {
                    return new ResponseEntity<>(ErrorStatus.NOT_ENOUGH_SPACE, HttpStatus.BAD_REQUEST);
                }

            } catch (Exception e) {
                logger.error(ErrorStatus.ELEMENT_NOT_FOUND.toString(), e);
                return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
        }
        try {
            purchaseRepository.save(purchase);
            shopStorageRepository.save(shopStorage);
            for(ShopStorageProduct shopStorageProduct : shopStorageProductList) {
                shopStorageProductRepository.save(shopStorageProduct);
            }
            return new ResponseEntity<>(purchase, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(ErrorStatus.ERROR_WHILE_SAVING.toString(), e);
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING, HttpStatus.NOT_FOUND);
        }
    }
}

