package ts.tsc.system.controller.product;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.controller.parent.BaseControllerInterface;
import ts.tsc.system.entity.product.Product;

public interface ProductControllerInterface extends BaseControllerInterface<Product> {
    ResponseEntity<?> findByCategory(String category);
}
