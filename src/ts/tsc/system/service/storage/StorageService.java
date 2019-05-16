package ts.tsc.system.service.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import ts.tsc.system.service.base.BaseService;

public interface StorageService<B, T, ID> extends BaseService<T, ID> {
    ResponseEntity<T>  findById(Long id, String stringQuery, JpaRepository<T, ID> repository);
    ResponseEntity<?> addStorage(ID id,
                                 T storage,
                                 JpaRepository<B, ID> repositoryBase,
                                 JpaRepository<T, ID> repositoryStorage);
}
