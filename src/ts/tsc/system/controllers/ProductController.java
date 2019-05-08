package ts.tsc.system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ts.tsc.system.entities.Product;
import ts.tsc.system.entities.Shop;
import ts.tsc.system.repositories.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/product")
public class ProductController {

    private final ProductRepository productRepository;

    @Autowired
    ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping(value = "/list")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Product>> findAll() {
        Iterable<Product> iterable = productRepository.findAll();
        List<Product> list = new ArrayList<>();
        iterable.forEach(list::add);
        if(list.size() > 0) {
            return ResponseEntity.ok().body(list);
        }else {
            return ResponseEntity.notFound().build();
        }
    }
}
