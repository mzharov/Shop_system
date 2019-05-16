package ts.tsc.system.services.interfaces;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.entities.NamedEntity;
import ts.tsc.system.entities.Product;
import ts.tsc.system.repositories.ProductRepository;

import java.util.List;

public interface ProductService extends NamedService <Product, Long>{
    ResponseEntity<List<Product>> findByCategory(String category,
                                                 ProductRepository productRepository);
}
