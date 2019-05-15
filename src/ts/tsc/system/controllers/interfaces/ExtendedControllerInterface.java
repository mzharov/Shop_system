package ts.tsc.system.controllers.interfaces;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ExtendedControllerInterface<T, S, P> extends BaseControllerInterface<T>{
    ResponseEntity<?> findStorageById(Long id);
    ResponseEntity<?> findAllStorages();
    ResponseEntity<?> addStorage(Long id, S storage);
    ResponseEntity<?> getProducts();
}
