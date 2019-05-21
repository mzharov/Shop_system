package ts.tsc.system.service.order;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.service.named.NamedService;

import java.util.List;

public interface SupplierOrderInterface extends NamedService<Supplier, Long> {
    ResponseEntity<?> deliverOrder(Long id);
    ResponseEntity<?> completeOrder(Long id);
    ResponseEntity<?> cancelOrder(Long id);
    ResponseEntity<?> receiveOrder(Long shopID,
                                   List<Long> productID,
                                   List<Integer> count);
}
