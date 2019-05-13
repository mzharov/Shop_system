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
    @PostMapping(value = "/delivery/{supplierID}/{shopID}/{productID}/{count}")
    public ResponseEntity<?> receiveDelivery(@PathVariable Long supplierID,
                             @PathVariable Long shopID,
                             @PathVariable Long productID,
                             @PathVariable int count) {
        Optional<SupplierStorage> supplierStorageOptional = supplierStorageRepository.findById(supplierID);
        Optional<ShopStorage> shopStorageOptional = shopStorageRepository.findById(shopID);
        Optional<Product> productOptional = productRepository.findById(productID);

        if(supplierStorageOptional.isPresent()
                && shopStorageOptional.isPresent()
                && productOptional.isPresent()) {
            //select entity from SupplierStorage entity where entity.supplier.id = ?1
            SupplierStorage supplierStorage = supplierStorageOptional.get();
            ShopStorage shopStorage = shopStorageOptional.get();
            Product product = productOptional.get();

            TypedQuery<SupplierStorageProduct> productTypedQuery = entityManager.createQuery(
                    "select p from SupplierStorageProduct p " +
                            "where  p.primaryKey.storage.id = ?1 " +
                            "and p.primaryKey.product.id = ?2 ",
                    SupplierStorageProduct.class).setParameter(1, supplierID).setParameter(2, shopID);
            SupplierStorageProduct supplierStorageProduct = productTypedQuery.getSingleResult();



            if (supplierStorageProduct !=null && supplierStorageProduct.getCount() > 0)  {
                if(supplierStorageProduct.getCount() < count) {
                    count = supplierStorageProduct.getCount();
                }

                Delivery delivery = new Delivery();
                delivery.setStatus(Status.RECEIVED);
                delivery.setShopStorage(shopStorage);
                delivery.setSupplierStorage(supplierStorage);

                DeliveryProduct deliveryProduct = new DeliveryProduct();
                deliveryProduct.setPrimaryKey(new DeliveryProductPrimaryKey(delivery, product));
                deliveryProduct.setCount(count);
                BigDecimal sum = supplierStorageProduct.getPrice().multiply(new BigDecimal(count));
                deliveryProduct.setPrice(sum);


                try {
                    BigDecimal price = supplierStorageProduct.getPrice();
                    supplierStorageProduct.setCount(supplierStorageProduct.getCount()-count);
                    supplierStorageProductRepository.save(supplierStorageProduct);
                    supplierStorage.setFreeSpace(supplierStorage.getFreeSpace()+count);
                    supplierStorageRepository.save(supplierStorage);

                    if(supplierStorageProduct.getCount() == 0) {
                        supplierStorageProductRepository.delete(supplierStorageProduct);
                    } else {
                        supplierStorageProductRepository.save(supplierStorageProduct);
                    }


                    deliveryRepository.save(delivery);
                    deliveryProductRepository.save(deliveryProduct);

                    ShopStorageProductPrimaryKey primaryKey =
                            new ShopStorageProductPrimaryKey(shopStorage, product);
                    Optional<ShopStorageProduct> shopStorageProductOptional
                            = shopStorageProductRepository.findById(primaryKey);

                    ShopStorageProduct shopStorageProduct;
                    if(shopStorageProductOptional.isPresent()) {
                        shopStorageProduct = shopStorageProductOptional.get();
                    } else  {
                        shopStorageProduct  = new ShopStorageProduct();
                        shopStorageProduct.setPrimaryKey(primaryKey);
                        shopStorageProduct.setCount(count);
                        shopStorageProduct.setPrice(price);
                    }

                    shopStorageProductRepository.save(shopStorageProduct);

                    return ResponseEntity.ok().body(delivery);
                } catch (Exception e) {
                    return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> transferDelivery(Long id) {
        Optional<Delivery> deliveryOptional = deliveryRepository.findById(id);
        if(deliveryOptional.isPresent()) {
            Delivery delivery = deliveryOptional.get();
            if(delivery.getStatus().equals(Status.RECEIVED.toString())) {
                delivery.setStatus(Status.DELIVERING);

                return null; //todo

            } else {
                return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> completeDelivery(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<?> cancelDelivery(Long id) {
        return null;
    }
}