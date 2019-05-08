package ts.tsc.system.services.interfaces;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ShopService<T> {
    ResponseEntity<List<T>> findAll();
    ResponseEntity<List<T>> findByName(String name);
    ResponseEntity<T> findById(Long id);
    ResponseEntity<T> save(T shop);
    ResponseEntity<?> delete(T shop);
}
