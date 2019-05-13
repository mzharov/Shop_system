package ts.tsc.system.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.entities.Shop;
import ts.tsc.system.repositories.ShopRepository;
import ts.tsc.system.services.interfaces.BaseService;
import ts.tsc.system.services.interfaces.NamedService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/shop")
public class ShopController {

    final Logger logger = LoggerFactory.getLogger(ShopController.class);

    private final ShopRepository repository;
    private final NamedService<Shop, Long> service;

    @Autowired
    public ShopController(ShopRepository repository,
                          NamedService<Shop, Long> service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<Shop>> findAll() {
        return service.findAll(repository);
    }

    @GetMapping(value = "/name/{name}")
    public ResponseEntity<List<Shop>> findByName(@PathVariable String name) {
        return service.findByName(name, repository);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Shop> findById(@PathVariable Long id) {
        return service.findById(id, repository);
    }


    @PostMapping(value = "/")
    public ResponseEntity<?> create(@RequestBody Shop shop) {
        return service.save(shop, repository);
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
        return service.delete(id, repository);
    }
}

