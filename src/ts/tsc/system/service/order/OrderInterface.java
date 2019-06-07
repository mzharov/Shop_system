package ts.tsc.system.service.order;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.service.named.NamedServiceInterface;

public interface OrderInterface<T extends NamedEntity, ID> extends NamedServiceInterface<T, ID> {
    ResponseEntity<?> deliverOrder(ID id);
    ResponseEntity<?> completeOrder(ID id);
    ResponseEntity<?> cancelOrder(ID id);
}
