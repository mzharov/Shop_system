package ts.tsc.system.service.product;

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
public class ProductServiceImplementation
        extends NamedServiceImplementation<Product, Long>
        implements ProductService {

    /**
     * Поиск по типу товара
     * @param category тип товара
     * @param productRepository репозиторий таблицы товаров
     * @return объект и код 200, если удалось найти список товаров; иначе код 404
     */
    @Transactional(readOnly = true)
    public ResponseEntity<List<Product>> findByCategory(String category, ProductRepository productRepository) {
        return productRepository.getByCategory(category).map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }
}
