package ts.tsc.system.controllers.parent;

import org.springframework.http.ResponseEntity;

public interface ExtendedControllerInterface<T, S> extends BaseControllerInterface<T>{
    ResponseEntity<?> findStorageById(Long id);
    ResponseEntity<?> findAllStorage();
    ResponseEntity<?> findStorageByOwnerId(Long id);
    ResponseEntity<?> getAllOrders();
    ResponseEntity<?> getOrderById(Long id);
    ResponseEntity<?> addStorage(Long id, S storage);
    ResponseEntity<?> getProducts();
}
