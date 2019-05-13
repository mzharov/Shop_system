package ts.tsc.system.controllers.interfaces;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.http.ResponseEntity;
import ts.tsc.system.entities.Product;

import java.util.List;

public interface ProductControllerInterface extends BaseControllerInterface<Product> {
    ResponseEntity<List<Product>> findByCategory(String category);
}
