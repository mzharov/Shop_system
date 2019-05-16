package ts.tsc.system.services.implementations;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ts.tsc.system.entities.Product;
import ts.tsc.system.repositories.ProductRepository;
import ts.tsc.system.services.interfaces.ProductService;

import java.util.List;

/**
 * Реализация интерфейса для поиска по типу товара
 */
@Service("productService")
public class ProductServiceImplementation
        extends NamedServiceImplementation<Product, Long>
        implements ProductService {

    /**
     * Поиск по типу товара
     * @param category тип товара
     * @param productRepository репозиторий таблицы товаров
     * @return объект и код 200, если удалось найти список товаров; иначе код 404
     */
    public ResponseEntity<List<Product>> findByCategory(String category, ProductRepository productRepository) {
        return productRepository.getByCategory(category).map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }
}
