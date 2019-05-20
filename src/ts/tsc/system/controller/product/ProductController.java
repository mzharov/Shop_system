package ts.tsc.system.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.repository.named.NamedRepository;
import ts.tsc.system.repository.product.ProductRepository;
import ts.tsc.system.service.base.BaseServiceImplementation;
import ts.tsc.system.service.product.ProductService;

import java.util.List;

@RestController
@RequestMapping(value = "/product")
public class ProductController implements ProductControllerInterface {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @Autowired
    ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    /**
     * Поиск всех товаров
     * @return {@link BaseServiceImplementation#findAll(JpaRepository)}
     */
    @Override
    @GetMapping(value = "/list")
    public ResponseEntity<?> findAll() {
        return productService.findAll(productRepository);
    }

    /**
     * Поиск товара по названию
     * @param name название товара
     * @return {@link ts.tsc.system.service.named.NamedServiceImplementation#findByName(String, NamedRepository)}
     */
    @Override
    @GetMapping(value = "/name/{name}")
    public ResponseEntity<?> findByName(@PathVariable String name) {
        return productService.findByName(name, productRepository);
    }

    /**
     * Поиск товара по идентификатору
     * @param id идентификатор
     * @return {@link ts.tsc.system.service.base.BaseServiceImplementation#findById(Object, JpaRepository)}
     */
    @Override
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return productService.findById(id, productRepository);
    }

    /**
     * Добавление нового товара
     * @param entity объект, представляющий товар
     * @return 1) код 400 с сообщением ID_CAN_NOT_BE_SET_IN_JSON, если в теле json задан идентификатор
     *         2) {@link ts.tsc.system.service.base.BaseServiceImplementation#save(Object, JpaRepository)}
     */
    @Override
    @PostMapping(value = "/")
    public ResponseEntity<?> create(@RequestBody Product entity) {
        if(entity.getId() !=null) {
            return new ResponseEntity<>(ErrorStatus.ID_CAN_NOT_BE_SET_IN_JSON, HttpStatus.BAD_REQUEST);
        }
        return productService.save(entity, productRepository);
    }

    /**
     * Обновление товара в таблице
     * @param id идентификатора
     * @param entity объект
     * @return 1) объект с кодом 200, если удалось обновить;
     *         2) код 404 - если не удалось найти объект с указанным идентификатором
     *         3) код 400 с сообщением ID_CAN_NOT_BE_SET_IN_JSON, если в теле json задан идентификатор
     */
    @Override
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product entity) {
        if(entity.getId() !=null) {
            return new ResponseEntity<>(ErrorStatus.ID_CAN_NOT_BE_SET_IN_JSON, HttpStatus.BAD_REQUEST);
        }
        return productRepository.findById(id)
                .map(record -> {
                    record.setName(entity.getName());
                    record.setCategory(entity.getCategory());
                    Product updated = productRepository.save(record);
                    return ResponseEntity.ok().body(updated);
                }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Поиск по типу товара
     * @param category категория
     * @return {@link ts.tsc.system.service.product.ProductServiceImplementation#findByCategory(String, ProductRepository)}
     */
    @Override
    @GetMapping(value = "/category/{category}")
    public ResponseEntity<List<Product>> findByCategory(@PathVariable String category) {
        return productService.findByCategory(category, productRepository);
    }
}
