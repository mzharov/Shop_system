package ts.tsc.system.controllers.parent;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ShopOrderInterface {
    ResponseEntity<?> receiveOrder(Long shopID,
                                   List<Long> productID,
                                   List<Integer> count);
}
