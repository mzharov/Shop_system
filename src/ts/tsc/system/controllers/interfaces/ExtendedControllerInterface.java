package ts.tsc.system.controllers.interfaces;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ExtendedControllerInterface<T, S, P> extends BaseControllerInterface<T>{
    ResponseEntity<List<S>> findStorageById(Long id);
    ResponseEntity<List<S>> findAllStorages();
    ResponseEntity<?> addStorage(Long id, S storage);
    ResponseEntity<List<P>> getProducts();
}
