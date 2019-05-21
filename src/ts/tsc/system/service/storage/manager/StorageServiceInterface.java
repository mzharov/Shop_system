package ts.tsc.system.service.storage.manager;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.service.base.BaseService;
import ts.tsc.system.service.named.NamedService;

public interface StorageServiceInterface<B extends NamedEntity<ID>, T, ID> extends BaseService<T, ID> {
    ResponseEntity<?>  findById(Long id, String stringQuery);
    ResponseEntity<?> addStorage(ID id, T storage,
                                 NamedService<B, ID> repositoryBase);
}
