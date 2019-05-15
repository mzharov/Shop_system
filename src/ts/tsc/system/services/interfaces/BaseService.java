package ts.tsc.system.services.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BaseService <T, ID> {
    ResponseEntity<List<T>> findAll(JpaRepository<T, ID> repository);
    ResponseEntity<T> findById(ID id, JpaRepository<T, ID> repository);
    ResponseEntity<?> save(T entity, JpaRepository<T, ID> repository);
    ResponseEntity<?> delete(ID id, JpaRepository<T, ID> repository);
}
