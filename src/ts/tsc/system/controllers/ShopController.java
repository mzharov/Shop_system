package ts.tsc.system.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.entities.Shop;
import ts.tsc.system.repositories.ShopRepository;
import ts.tsc.system.serialization.Shops;
import ts.tsc.system.services.ShopService;

import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/shop")
public class ShopController {

    final Logger logger = LoggerFactory.getLogger(ShopController.class);

    private final ShopRepository shopService;

    @Autowired
    public ShopController(ShopRepository shopService) {
        this.shopService = shopService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/list")
    public Shops getShops() {
        Iterable<Shop> iterable = shopService.findAll();
        List<Shop> shops = new ArrayList<>();
        iterable.forEach(shops::add);
        return new Shops(shops);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Shop> findShopById(@PathVariable Long id) {
        return shopService.findById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping(value = "/")
    public ResponseEntity<?> create(@RequestBody Shop shop) {
        try {
            shopService.save(shop);
            return ResponseEntity.ok().body(shop);
        } catch (Exception e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Shop> update(@PathVariable Long id, @RequestBody Shop shop) {
        return shopService.findById(id)
                .map(record -> {
                    record.setName(shop.getName());
                    record.setBudget(shop.getBudget());
                    Shop updated = shopService.save(record);
                    return ResponseEntity.ok().body(updated);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return shopService.findById(id)
                .map(record -> {
                    shopService.deleteById(id);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}

