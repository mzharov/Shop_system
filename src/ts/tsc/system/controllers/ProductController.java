package ts.tsc.system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ts.tsc.system.entities.Product;
import ts.tsc.system.repositories.ProductRepository;
import ts.tsc.system.services.interfaces.BaseService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/product")
public class ProductController {

    private final BaseService<Product, Long> productService;
    private final ProductRepository productRepository;

    @Autowired
    ProductController(BaseService<Product, Long> productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @GetMapping(value = "/list")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Product>> findAll() {
        return productService.findAll(productRepository);
    }
}
