package ts.tsc.system.service.order;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.service.named.NamedServiceInterface;

public interface OrderInterface<T extends NamedEntity> extends NamedServiceInterface<T, Long> {
    ResponseEntity<?> deliverOrder(Long id);
    ResponseEntity<?> completeOrder(Long id);
    ResponseEntity<?> cancelOrder(Long id);
}
