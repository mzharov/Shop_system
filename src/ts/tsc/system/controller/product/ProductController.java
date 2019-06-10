package ts.tsc.system.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.controller.parent.NamedController;
import ts.tsc.system.controller.response.BaseResponseBuilder;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.service.product.ProductService;
import ts.tsc.system.service.product.ProductServiceInterface;

@RestController
@RequestMapping(value = "/app/product")
public class ProductController extends NamedController<Product, ProductServiceInterface, Long> {

    private final ProductServiceInterface productService;
    private final BaseResponseBuilder<Product> productBaseResponseBuilder;

    @Autowired
    public ProductController(@Qualifier("productService") ProductServiceInterface productService,
                           BaseResponseBuilder<Product> productBaseResponseBuilder) {
        this.productService = productService;
        this.productBaseResponseBuilder = productBaseResponseBuilder;
    }


    @Override
    protected BaseResponseBuilder<Product> getResponseBuilder() {
        return productBaseResponseBuilder;
    }

    @Override
    protected ProductServiceInterface getService() {
        return productService;
    }

    /**
     * Поиск по типу товара
     * @param category категория
     * @return {@link ProductService#findByCategory(String)}
     */
    @GetMapping(value = "/category/{category}")
    public ResponseEntity<?> findByCategory(@PathVariable String category) {
        return productBaseResponseBuilder.getAll(productService.findByCategory(category));
    }
}
