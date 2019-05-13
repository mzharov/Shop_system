package ts.tsc.system.services.implementations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.services.interfaces.BaseService;

import java.util.ArrayList;
import java.util.List;

@Service("baseService")
@Transactional
public class BaseServiceImplementation <T,I> implements BaseService<T, I> {


    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<T>> findAll(JpaRepository<T,I> repository) {
        Iterable<T> iterable = repository.findAll();
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        if(list.size() > 0) {
            return ResponseEntity.ok().body(list);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<T> findById(I id, JpaRepository<T,I> repository) {
        return repository.findById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<?> save(T entity, JpaRepository<T,I> repository) {
        try {
            repository.save(entity);
            return ResponseEntity.ok().body(entity);
        } catch (Exception e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> delete(I id, JpaRepository<T,I> repository) {
        return repository.findById(id)
                .map(record -> {
                    repository.deleteById(id);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}
