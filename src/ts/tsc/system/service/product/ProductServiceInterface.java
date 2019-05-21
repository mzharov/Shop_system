package ts.tsc.system.service.product;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.service.named.NamedServiceInterface;

import java.util.List;

public interface ProductServiceInterface extends NamedServiceInterface<Product, Long> {
    ResponseEntity<List<Product>> findByCategory(String category);
}
