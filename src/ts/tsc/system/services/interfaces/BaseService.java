package ts.tsc.system.services.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BaseService <T, I> {
    ResponseEntity<List<T>> findAll(JpaRepository<T,I> repository);
    ResponseEntity<T> findById(I id, JpaRepository<T,I> repository);
    ResponseEntity<?> save(T entity, JpaRepository<T,I> repository);
    ResponseEntity<?> delete(I id, JpaRepository<T,I> repository);
}
