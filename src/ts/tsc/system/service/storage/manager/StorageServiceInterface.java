package ts.tsc.system.service.storage.manager;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.service.base.BaseServiceInterface;
import ts.tsc.system.service.named.NamedServiceInterface;

public interface StorageServiceInterface<B extends NamedEntity<ID>, T, ID> extends BaseServiceInterface<T, ID> {
    ResponseEntity<?>  findById(Long id, String stringQuery);
    ResponseEntity<?> addStorage(ID id, T storage,
                                 NamedServiceInterface<B, ID> repositoryBase);
}
