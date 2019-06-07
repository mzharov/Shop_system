package ts.tsc.system.controller.parent;

import org.springframework.http.ResponseEntity;

public interface BaseControllerInterface<T, ID> {
    ResponseEntity<?> findAll();
    ResponseEntity<?> findById(ID id);
    ResponseEntity<?> create(T entity);
    ResponseEntity<?> update(ID id, T entity);
}
