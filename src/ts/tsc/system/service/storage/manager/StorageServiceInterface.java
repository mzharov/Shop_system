package ts.tsc.system.service.storage.manager;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.service.base.BaseServiceInterface;

import java.util.List;

public interface StorageServiceInterface<STORAGE, ID>
        extends BaseServiceInterface<STORAGE, ID> {
    List<STORAGE> findStoragesByOwnerId(ID id);
    ResponseEntity<?> addStorage(ID id, STORAGE storage);
}
