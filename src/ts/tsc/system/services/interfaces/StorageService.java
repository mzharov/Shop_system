package ts.tsc.system.services.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StorageService<B, T, ID> extends BaseService<T, ID>{
    ResponseEntity<T>  findById(Long id, String stringQuery, JpaRepository<T, ID> repository);
    ResponseEntity<?> addStorage(ID id,
                                 T storage,
                                 JpaRepository<B, ID> repositoryBase,
                                 JpaRepository<T, ID> repositoryStorage);
}
