package ts.tsc.system.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.entities.Supplier;
import ts.tsc.system.entities.SupplierStorage;
import ts.tsc.system.entities.SupplierStorageProduct;
import ts.tsc.system.repositories.SupplierRepository;
import ts.tsc.system.repositories.SupplierStorageProductRepository;
import ts.tsc.system.repositories.SupplierStorageRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/supplier")
public class SupplierController {

    final Logger logger = LoggerFactory.getLogger(ShopController.class);

    private final SupplierRepository supplierRepository;
    private final SupplierStorageRepository supplierStorageRepository;
    private final SupplierStorageProductRepository supplierStorageProductRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public SupplierController(SupplierRepository supplierRepository,
                              SupplierStorageRepository supplierRepositoryService,
                              SupplierStorageProductRepository supplierStorageProductRepository) {
        this.supplierRepository = supplierRepository;
        this.supplierStorageRepository = supplierRepositoryService;
        this.supplierStorageProductRepository = supplierStorageProductRepository;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/list")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Supplier>> getSuppliers() {
        Iterable<Supplier> iterable = supplierRepository.findAll();
        List<Supplier> list = new ArrayList<>();
        iterable.forEach(list::add);
        if(list.size() > 0) {
            return ResponseEntity.ok().body(list);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/name/{name}")
    @Transactional(readOnly = true)
    public ResponseEntity<Supplier> findByName(@PathVariable String name) {
        return supplierRepository.findByName(name).map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/product/list")
    @Transactional(readOnly = true)
    public ResponseEntity<List<SupplierStorageProduct>> getProducts() {
        Iterable<SupplierStorageProduct> iterable = supplierStorageProductRepository.findAll();
        List<SupplierStorageProduct> list = new ArrayList<>();
        iterable.forEach(list::add);
        if(list.size() > 0) {
            return ResponseEntity.ok().body(list);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Supplier> findSupplierById(@PathVariable Long id) {
        return supplierRepository.findById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/storage/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<SupplierStorage>> findStorageById(@PathVariable Long id) {
        TypedQuery<SupplierStorage> query = entityManager
                .createQuery("select ss from SupplierStorage ss where ss.supplier.id = ?1",
                        SupplierStorage.class);
        query.setParameter(1, id);
        List<SupplierStorage> storage = query.getResultList();

        if(storage.size() > 0) {
            return ResponseEntity.ok().body(storage);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/storage/list")
    public ResponseEntity<List<SupplierStorage>> getStorages() {
        Iterable<SupplierStorage> iterable = supplierStorageRepository.findAll();
        List<SupplierStorage> list = new ArrayList<>();
        iterable.forEach(list::add);
        if(list.size() > 0) {
            return ResponseEntity.ok().body(list);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> create(@RequestBody Supplier supplier) {
        try {
            supplierRepository.save(supplier);
            return ResponseEntity.ok().body(supplier);
        } catch (Exception e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<?> addStorage(@PathVariable Long id, @RequestBody SupplierStorage storage) {
        try {
            Optional<Supplier> optionalSupplier = supplierRepository.findById(id);
            if(optionalSupplier.isPresent()) {
                Supplier supplier = optionalSupplier.get();
                storage.setSupplier(supplier);
                supplierStorageRepository.save(storage);
                return ResponseEntity.ok().body(storage);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        return supplierRepository.findById(id)
                .map(record -> {
                    supplierRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}