package ts.tsc.system.controller.parent;

import org.springframework.http.ResponseEntity;

public interface ExtendedControllerInterface<T, S> extends NamedControllerInterface<T>{
    ResponseEntity<?> findStorageById(Long id);
    ResponseEntity<?> findAllStorage();
    ResponseEntity<?> findStorageByOwnerId(Long id);
    ResponseEntity<?> getAllOrders();
    ResponseEntity<?> getOrderById(Long id);
    ResponseEntity<?> addStorage(Long id, S storage);
    ResponseEntity<?> getStorageProducts(Long id);
}
