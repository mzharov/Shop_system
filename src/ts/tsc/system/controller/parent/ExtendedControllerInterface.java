package ts.tsc.system.controller.parent;

import org.springframework.http.ResponseEntity;

public interface ExtendedControllerInterface<T, S, ID> extends NamedControllerInterface<T, ID>{
    ResponseEntity<?> findStorageById(ID id);
    ResponseEntity<?> findAllStorage();
    ResponseEntity<?> findStoragesByOwnerId(ID id);
    ResponseEntity<?> getAllOrders();
    ResponseEntity<?> getOrderById(ID id);
    ResponseEntity<?> addStorage(ID id, S storage);
    ResponseEntity<?> getStorageProducts(ID id);
}
