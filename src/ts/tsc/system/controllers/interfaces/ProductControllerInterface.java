package ts.tsc.system.controllers.interfaces;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.entities.Product;

public interface ProductControllerInterface extends BaseControllerInterface<Product> {
    ResponseEntity<?> findByCategory(String category);
}
