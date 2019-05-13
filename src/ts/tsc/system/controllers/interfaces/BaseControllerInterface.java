package ts.tsc.system.controllers.interfaces;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BaseControllerInterface<T> {
    ResponseEntity<List<T>> findAll();
    ResponseEntity<List<T>> findByName(String name);
    ResponseEntity<T> findById(Long id);
    ResponseEntity<?> create(T entity);
    ResponseEntity<T> update(Long id, T entity);
    ResponseEntity<?> delete(Long id);
}
