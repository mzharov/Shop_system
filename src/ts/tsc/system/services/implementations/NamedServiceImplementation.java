package ts.tsc.system.services.implementations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ts.tsc.system.repositories.NamedRepository;
import ts.tsc.system.services.interfaces.BaseService;
import ts.tsc.system.services.interfaces.NamedService;

import java.util.List;

//@Service("namedService")
public class NamedServiceImplementation<T,I>
        extends BaseServiceImplementation<T,I>
        implements NamedService<T,I> {

    @Override
    public ResponseEntity<List<T>> findByName(String name, NamedRepository<T, I> repository) {
        return repository.findByName(name).map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }
}
