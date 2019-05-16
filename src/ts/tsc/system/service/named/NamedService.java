package ts.tsc.system.service.named;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.repository.NamedRepository;
import ts.tsc.system.service.base.BaseService;

public interface NamedService<T extends NamedEntity, I> extends BaseService<T,I> {
    ResponseEntity<?> findByName(String name, NamedRepository<T, I> repository);
}
