package ts.tsc.system.controllers.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.controllers.interfaces.ProductControllerInterface;
import ts.tsc.system.entities.Product;
import ts.tsc.system.entities.Shop;
import ts.tsc.system.repositories.ProductRepository;
import ts.tsc.system.services.interfaces.BaseService;
import ts.tsc.system.services.interfaces.NamedService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/product")
public class ProductController implements ProductControllerInterface {

    private final NamedService<Product, Long> productService;
    private final ProductRepository productRepository;

    @Autowired
    ProductController(NamedService<Product, Long> productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @Override
    @GetMapping(value = "/list")
    public ResponseEntity<List<Product>> findAll() {
        return productService.findAll(productRepository);
    }

    @Override
    @GetMapping(value = "/name/{name}")
    public ResponseEntity<List<Product>> findByName(@PathVariable String name) {
        return productService.findAll(productRepository);
    }

    @Override
    @GetMapping(value = "/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        return productService.findById(id, productRepository);
    }

    @Override
    @PostMapping(value = "/")
    public ResponseEntity<?> create(@RequestBody Product entity) {
        return productService.save(entity, productRepository);
    }

    @Override
    @PutMapping(value = "/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product entity) {
        return productRepository.findById(id)
                .map(record -> {
                    record.setName(entity.getName());
                    record.setCategory(entity.getCategory());
                    Product updated = productRepository.save(record);
                    return ResponseEntity.ok().body(updated);
                }).orElse(ResponseEntity.notFound().build());
    }

    @Override
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return productService.delete(id, productRepository);
    }

    @Override
    @GetMapping(value = "/category/{category}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Product>> findByCategory(@PathVariable String category) {
        return productRepository.getByCategory(category).map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }
}
