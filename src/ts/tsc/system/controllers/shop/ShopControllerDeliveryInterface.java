package ts.tsc.system.controllers.shop;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.controllers.status.enums.Status;

import java.util.List;

public interface ShopControllerDeliveryInterface {
    ResponseEntity<?> receiveOrder(Long shopID,
                                   List<Long> productID,
                                   List<Integer> count);
    ResponseEntity<?> deliverOrder(Long id);
    ResponseEntity<?> completeOrder(Long id);
    ResponseEntity<?> cancelOrder(Long id);
    ResponseEntity<?> changeStatus(Long id, Status status);
}
