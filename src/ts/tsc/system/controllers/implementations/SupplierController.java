package ts.tsc.system.controllers.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.controllers.interfaces.ExtendedControllerInterface;
import ts.tsc.system.entities.Supplier;
import ts.tsc.system.entities.SupplierStorage;
import ts.tsc.system.entities.SupplierStorageProduct;
import ts.tsc.system.entities.SupplierStorageProductPrimaryKey;
import ts.tsc.system.repositories.SupplierRepository;
import ts.tsc.system.repositories.SupplierStorageProductRepository;
import ts.tsc.system.repositories.SupplierStorageRepository;
import ts.tsc.system.services.interfaces.BaseService;
import ts.tsc.system.services.interfaces.NamedService;
import ts.tsc.system.services.interfaces.StorageService;

import java.util.List;

@RestController
@RequestMapping(value = "/supplier")
public class SupplierController implements ExtendedControllerInterface<Supplier, SupplierStorage, SupplierStorageProduct> {

    final Logger logger = LoggerFactory.getLogger(ShopController.class);

    private final SupplierRepository supplierRepository;
    private final NamedService<Supplier, Long> supplierService;
    private final StorageService<Supplier, SupplierStorage, Long> storageService;
    private final SupplierStorageRepository supplierStorageRepository;
    private final SupplierStorageProductRepository productRepository;
    private final BaseService<SupplierStorageProduct, SupplierStorageProductPrimaryKey> productService;

    @Autowired
    public SupplierController(SupplierRepository supplierRepository,
                              StorageService<Supplier, SupplierStorage, Long> storageService,
                              SupplierStorageRepository supplierStorageRepository,
                              NamedService<Supplier, Long> supplierService,
                              SupplierStorageProductRepository productRepository,
                              @Qualifier(value = "baseService") BaseService<SupplierStorageProduct,
                                      SupplierStorageProductPrimaryKey> productService) {
        this.supplierRepository = supplierRepository;
        this.storageService = storageService;
        this.supplierStorageRepository = supplierStorageRepository;
        this.supplierService = supplierService;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<Supplier>> findAll() {
        return supplierService.findAll(supplierRepository);
    }

    @GetMapping(value = "/name/{name}")
    public ResponseEntity<List<Supplier>> findByName(@PathVariable String name) {
        return supplierService.findByName(name, supplierRepository);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Supplier> findById(@PathVariable Long id) {
        return supplierService.findById(id, supplierRepository);
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> create(@RequestBody Supplier supplier) {
        return supplierService.save(supplier, supplierRepository);
    }


    @PutMapping(value = "/{id}")
    public ResponseEntity<Supplier> update(@PathVariable Long id, @RequestBody Supplier supplier) {
        return supplierRepository.findById(id)
                .map(record -> {
                    record.setName(supplier.getName());
                    Supplier updated = supplierRepository.save(record);
                    return ResponseEntity.ok().body(updated);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return supplierService.delete(id, supplierRepository);
    }

    @GetMapping(value = "/storage/{id}")
    public ResponseEntity<List<SupplierStorage>> findStorageById(@PathVariable Long id) {
        String stringQuery = "select entity from SupplierStorage entity where entity.supplier.id = ?1";
        return storageService.findById(id, stringQuery, supplierStorageRepository);
    }

    @GetMapping(value = "/storage/list")
    public ResponseEntity<List<SupplierStorage>> findAllStorages() {
        return storageService.findAll(supplierStorageRepository);
    }

    @PostMapping(value = "/storage/{id}")
    public ResponseEntity<?> addStorage(@PathVariable Long id, @RequestBody SupplierStorage storage) {
        return storageService.addStorage(id, storage, supplierRepository, supplierStorageRepository);
    }

    @GetMapping(value = "/storage/product/list")
    public ResponseEntity<List<SupplierStorageProduct>> getProducts() {
        return productService.findAll(productRepository);
    }
}