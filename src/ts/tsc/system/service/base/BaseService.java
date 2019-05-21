package ts.tsc.system.service.base;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BaseService <T, ID> {
    List<T> findAll();
    Optional<T> findById(ID id);
    T save(T entity);
    JpaRepository<T, ID> getRepository();
}
