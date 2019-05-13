package ts.tsc.system.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.entities.*;
import ts.tsc.system.repositories.*;
import ts.tsc.system.services.interfaces.BaseService;
import ts.tsc.system.services.interfaces.NamedService;
import ts.tsc.system.services.interfaces.StorageService;

import java.util.List;

@RestController
@RequestMapping(value = "/shop")
public class ShopController {

    final Logger logger = LoggerFactory.getLogger(ShopController.class);

    private final ShopRepository shopRepository;
    private final NamedService<Shop, Long> shopService;
    private final StorageService<Shop, ShopStorage, Long> storageService;
    private final ShopStorageRepository storageRepository;
    private final ShopStorageProductRepository productRepository;
    private final BaseService<ShopStorageProduct, ShopStorageProductPrimaryKey> productService;

    @Autowired
    public ShopController(ShopRepository shopRepository,
                          NamedService<Shop, Long> shopService,
                          StorageService<Shop, ShopStorage, Long> storageService,
                          ShopStorageRepository storageRepository,
                          ShopStorageProductRepository productRepository,
                          @Qualifier(value = "baseService")
                                      BaseService<ShopStorageProduct, ShopStorageProductPrimaryKey> productService) {
        this.shopRepository = shopRepository;
        this.shopService = shopService;
        this.storageService = storageService;
        this.storageRepository = storageRepository;
        this.productRepository = productRepository;
        this.productService = productService;
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
        String stringQuery = "select entity from SupplierStorage entity where entity.supplier.id = ?1";
        return storageService.findById(id, stringQuery, storageRepository);
    }


    @GetMapping(value = "/storage/list")
    public ResponseEntity<List<ShopStorage>> findAllStorages() {
        return storageService.findAll(storageRepository);
    }

    @PostMapping(value = "/storage/{id}")
    public ResponseEntity<?> addStorage(@PathVariable Long id, @RequestBody ShopStorage storage) {
        return storageService.addStorage(id, storage, shopRepository, storageRepository);
    }

    @GetMapping(value = "/storage/product/list")
    public ResponseEntity<List<ShopStorageProduct>> getProducts() {
        return productService.findAll(productRepository);
    }
}

