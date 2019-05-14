package ts.tsc.system.controllers.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import ts.tsc.system.controllers.status.Status;
import ts.tsc.system.entities.Supplier;
import ts.tsc.system.entities.SupplierStorage;
import ts.tsc.system.entities.SupplierStorageProduct;

import java.math.BigDecimal;

public interface SupplierControllerInterface
        extends ExtendedControllerInterface<Supplier, SupplierStorage, SupplierStorageProduct> {
    ResponseEntity<?> receiveDelivery(Long supplierID, Long shopID, Long productID, int count);
    ResponseEntity<?> transferDelivery(Long id);
    ResponseEntity<?> completeDelivery(Long id);
    ResponseEntity<?> cancelDelivery(Long id);
    ResponseEntity<?> changeStatus(Status status, Long id);
}
