package ts.tsc.system.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.entities.Shop;
import ts.tsc.system.entities.SupplierStorage;
import ts.tsc.system.repositories.ShopRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/shop")
public class ShopController {

    final Logger logger = LoggerFactory.getLogger(ShopController.class);

    private final ShopRepository repository;

    @Autowired
    public ShopController(ShopRepository repository) {
        this.repository = repository;
    }

    @GetMapping(value = "/list")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Shop>> findAll() {
        Iterable<Shop> iterable = repository.findAll();
        List<Shop> list = new ArrayList<>();
        iterable.forEach(list::add);
        if(list.size() > 0) {
            return ResponseEntity.ok().body(list);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/name/{name}")
    @Transactional(readOnly = true)
    public ResponseEntity<Shop> findByName(@PathVariable String name) {
        return repository.findByName(name).map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Shop> findShopById(@PathVariable Long id) {
        return repository.findById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping(value = "/")
    public ResponseEntity<?> create(@RequestBody Shop shop) {
        try {
            repository.save(shop);
            return ResponseEntity.ok().body(shop);
        } catch (Exception e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping(value = "/{id}")
    public ResponseEntity<Shop> update(@PathVariable Long id, @RequestBody Shop shop) {
        return repository.findById(id)
                .map(record -> {
                    record.setName(shop.getName());
                    record.setBudget(shop.getBudget());
                    Shop updated = repository.save(record);
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

