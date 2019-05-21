package ts.tsc.system.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.repository.product.ProductRepository;
import ts.tsc.system.service.named.NamedServiceImplementation;

import java.util.List;

/**
 * Реализация интерфейса для поиска по типу товара
 */
@Service("productService")
@Transactional
public class ProductService
        extends NamedServiceImplementation<Product, Long>
        implements ProductServiceInterface {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Поиск по типу товара
     * @param category тип товара
     * @return объект и код 200, если удалось найти список товаров; иначе код 404
     */
    @Transactional(readOnly = true)
    public ResponseEntity<List<Product>> findByCategory(String category) {
        return productRepository.getByCategory(category).map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public Product update(Long id, Product product) {
        return productRepository.findById(id)
                .map(record -> {
                    record.setName(product.getName());
                    record.setCategory(product.getCategory());
                    return productRepository.save(record);
                }).orElse(null);
    }

    @Override
    public ProductRepository getRepository() {
        return this.productRepository;
    }
}
