package ts.tsc.system.service.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface BaseService <T, ID> {
    ResponseEntity<?> findAll(JpaRepository<T, ID> repository);
    ResponseEntity<?> findById(ID id, JpaRepository<T, ID> repository);
    ResponseEntity<?> save(T entity, JpaRepository<T, ID> repository);
    List<T> findAll();
    Optional<T> findById(ID id);
    T save(T entity);
    T update(ID id, T entity);
    JpaRepository<T, ID> getRepository();
}
