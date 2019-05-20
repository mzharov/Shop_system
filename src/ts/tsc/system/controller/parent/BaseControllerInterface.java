package ts.tsc.system.controller.parent;

import org.springframework.http.ResponseEntity;

public interface BaseControllerInterface<T> {
    ResponseEntity<?> findAll();
    ResponseEntity<?> findByName(String name);
    ResponseEntity<?> findById(Long id);
    ResponseEntity<?> create(T entity);
    ResponseEntity<?> update(Long id, T entity);
}
