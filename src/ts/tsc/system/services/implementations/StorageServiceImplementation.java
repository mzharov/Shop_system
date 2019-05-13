package ts.tsc.system.services.implementations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entities.interfaces.BaseStorage;
import ts.tsc.system.services.interfaces.StorageService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Service("storageService")
@Transactional
public class StorageServiceImplementation<B, T extends BaseStorage<B>, ID>
        extends BaseServiceImplementation<T, ID> implements StorageService<B, T, ID>{
    @PersistenceContext
    EntityManager entityManager;

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public ResponseEntity<List<T>> findById(Long id,
                                         String stringQuery,
                                         JpaRepository<T, ID> repository) {
        Query query = entityManager.createNamedQuery(stringQuery)
                .setParameter(1, id);
        List<T> storage = query.getResultList();

        if(storage.size() > 0) {
            return ResponseEntity.ok().body(storage);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<?> addStorage(ID id, T storage,
                                        JpaRepository<B, ID> repositoryBase,
                                        JpaRepository<T, ID> repositoryStorage) {
        try {
            Optional<B> optionalSupplier = repositoryBase.findById(id);
            if(optionalSupplier.isPresent()) {
                B base = optionalSupplier.get();
                storage.setOwner(base);
                repositoryStorage.save(storage);
                return ResponseEntity.ok().body(storage);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
