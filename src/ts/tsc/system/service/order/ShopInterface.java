package ts.tsc.system.service.order;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.service.named.NamedService;

import java.util.List;

public interface ShopInterface extends NamedService<Shop, Long> {
    ResponseEntity<?> deliverOrder(Long id);
    ResponseEntity<?> completeOrder(Long id);
    ResponseEntity<?> cancelOrder(Long id);
    ResponseEntity<?> receiveOrder(Long shopID,
                                   List<Long> productID,
                                   List<Integer> count);
}
