package ts.tsc.system.controllers.interfaces;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BaseControllerInterface<T> {
    ResponseEntity<?> findAll();
    ResponseEntity<?> findByName(String name);
    ResponseEntity<?> findById(Long id);
    ResponseEntity<?> create(T entity);
    ResponseEntity<?> update(Long id, T entity);
    ResponseEntity<?> delete(Long id);
}
