package ts.tsc.system.service.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

public interface BaseService <T, ID> {
    ResponseEntity<?> findAll(JpaRepository<T, ID> repository);
    ResponseEntity<?> findById(ID id, JpaRepository<T, ID> repository);
    ResponseEntity<?> save(T entity, JpaRepository<T, ID> repository);
}
