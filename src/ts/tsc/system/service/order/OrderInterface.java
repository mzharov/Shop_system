package ts.tsc.system.service.order;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.service.named.NamedService;

public interface OrderInterface<T extends NamedEntity> extends NamedService<T, Long> {
    ResponseEntity<?> deliverOrder(Long id);
    ResponseEntity<?> completeOrder(Long id);
    ResponseEntity<?> cancelOrder(Long id);
}
