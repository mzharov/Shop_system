package ts.tsc.system.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.entities.Supplier;
import ts.tsc.system.entities.SupplierStorage;
import ts.tsc.system.repositories.SupplierRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/supplier")
public class SupplierController {

    final Logger logger = LoggerFactory.getLogger(ShopController.class);

    private final SupplierRepository repository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public SupplierController(SupplierRepository shopService) {
        this.repository = shopService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/list")
    public List<Supplier> getSuppliers() {
        Iterable<Supplier> iterable = repository.findAll();
        List<Supplier> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Supplier> findSupplierById(@PathVariable Long id) {
        return repository.findById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/storage/{id}")
    public ResponseEntity<List<SupplierStorage>> findStorageById(@PathVariable Long id) {
        TypedQuery<SupplierStorage> query = entityManager
                .createQuery("select ss from SupplierStorage ss where ss.supplier.id = ?1",
                        SupplierStorage.class);
        query.setParameter(1, id);
        List<SupplierStorage> storage = query.getResultList();

        if(storage !=null) {
            return ResponseEntity.ok().body(storage);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> create(@RequestBody Supplier supplier) {
        try {
            repository.save(supplier);
            return ResponseEntity.ok().body(supplier);
        } catch (Exception e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Supplier> update(@PathVariable Long id, @RequestBody Supplier supplier) {
        return repository.findById(id)
                .map(record -> {
                    record.setName(supplier.getName());
                    Supplier updated = repository.save(record);
                    return ResponseEntity.ok().body(updated);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(record -> {
                    repository.deleteById(id);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}