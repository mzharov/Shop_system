package ts.tsc.system.services.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ts.tsc.system.entities.BaseEntity;
import ts.tsc.system.entities.Shop;

import java.util.List;

public interface BaseService <T, ID> {
    ResponseEntity<List<T>> findAll(JpaRepository<T, ID> repository);
    ResponseEntity<T> findById(ID id, JpaRepository<T, ID> repository);
    ResponseEntity<?> save(T entity, JpaRepository<T, ID> repository);
    ResponseEntity<?> delete(ID id, JpaRepository<T, ID> repository);
}
