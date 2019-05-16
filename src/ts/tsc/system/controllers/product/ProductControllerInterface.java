package ts.tsc.system.controllers.product;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.controllers.interfaces.BaseControllerInterface;
import ts.tsc.system.entity.product.Product;

public interface ProductControllerInterface extends BaseControllerInterface<Product> {
    ResponseEntity<?> findByCategory(String category);
}
