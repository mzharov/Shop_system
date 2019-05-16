package ts.tsc.system.controllers.parent;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SupplierOrderInterface {
    ResponseEntity<?> receiveOrder(Long supplierID,
                                   Long shopID,
                                   List<Long> productID,
                                   List<Integer> count);
}
