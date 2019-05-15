package ts.tsc.system.services.interfaces;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.entities.NamedEntity;
import ts.tsc.system.repositories.NamedRepository;

import java.util.List;

public interface NamedService<T extends NamedEntity, I> extends BaseService <T,I>{
    ResponseEntity<?> findByName(String name, NamedRepository<T, I> repository);
}
