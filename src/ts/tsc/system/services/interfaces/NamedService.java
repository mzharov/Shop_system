package ts.tsc.system.services.interfaces;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.repositories.NamedRepository;

import java.util.List;

public interface NamedService<T, I> extends BaseService <T,I>{
    ResponseEntity<List<T>> findByName(String name, NamedRepository<T, I> repository);
}
