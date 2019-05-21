package ts.tsc.system.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.controller.parent.BaseControllerInterface;
import ts.tsc.system.controller.response.BaseResponseBuilder;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.service.base.BaseService;
import ts.tsc.system.service.named.NamedService;
import ts.tsc.system.service.product.ProductService;
import ts.tsc.system.service.product.ProductServiceInterface;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/product")
public class ProductController implements BaseControllerInterface<Product> {

    private final ProductServiceInterface productService;
    private final BaseResponseBuilder<Product> productBaseResponseBuilder;

    @Autowired
    ProductController(@Qualifier("productService") ProductServiceInterface productService,
                      BaseResponseBuilder<Product> productBaseResponseBuilder) {
        this.productService = productService;
        this.productBaseResponseBuilder = productBaseResponseBuilder;
    }

    /**
     * Поиск всех товаров
     * @return {@link BaseService#findAll()}
     */
    @Override
    @GetMapping(value = "/list")
    public ResponseEntity<?> findAll() {
        return productBaseResponseBuilder.getAll(productService.findAll());
    }

    /**
     * Поиск товара по названию
     * @param name название товара
     * @return {@link NamedService#findByName(String)}
     */
    @Override
    @GetMapping(value = "/name/{name}")
    public ResponseEntity<?> findByName(@PathVariable String name) {
        return productBaseResponseBuilder.getAll(productService.findByName(name));
    }

    /**
     * Поиск товара по идентификатору
     * @param id идентификатор
     * @return {@link BaseService#findById(Object)}
     */
    @Override
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<Product> deliveryOptional = productService.findById(id);
        return deliveryOptional.<ResponseEntity<?>>map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    /**
     * Добавление нового товара
     * @param product объект, представляющий товар
     * @return 1) код 400 с сообщением ID_CAN_NOT_BE_SET_IN_JSON, если в теле json задан идентификатор
     *         2) {@link ProductService#save(Object)}
     */
    @Override
    @PostMapping(value = "/")
    public ResponseEntity<?> create(@RequestBody Product product) {
        if(product.getId() !=null) {
            return new ResponseEntity<>(ErrorStatus.ID_CAN_NOT_BE_SET_IN_JSON, HttpStatus.BAD_REQUEST);
        }
        return productBaseResponseBuilder.save(productService.save(product));
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
        Optional<Product> productOptional = productService.findById(id);
        if(productOptional.isPresent()) {
            return productBaseResponseBuilder.save(productService.update(id, productOptional.get()));
        } else {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":product", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Поиск по типу товара
     * @param category категория
     * @return {@link ProductService#findByCategory(String)}
     */
    @GetMapping(value = "/category/{category}")
    public ResponseEntity<List<Product>> findByCategory(@PathVariable String category) {
        return productService.findByCategory(category);
    }
}
