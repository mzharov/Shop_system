package ts.tsc.system.controllers.supplier;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.controllers.status.enums.Status;

import java.util.List;

public interface SupplierControllerDeliveryInterface {
    ResponseEntity<?> receiveDelivery(Long supplierID,
                                      Long shopID,
                                      List<Long> productID,
                                      List<Integer> count);
    ResponseEntity<?> transferDelivery(Long id);
    ResponseEntity<?> completeDelivery(Long id);
    ResponseEntity<?> cancelDelivery(Long id);
    ResponseEntity<?> changeStatus(Long id, Status status);
}
