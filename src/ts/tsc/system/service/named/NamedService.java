package ts.tsc.system.service.named;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.repository.named.NamedRepository;
import ts.tsc.system.service.base.BaseService;

public interface NamedService<T extends NamedEntity, ID> extends BaseService<T, ID> {
    ResponseEntity<?> findByName(String name, NamedRepository<T, ID> repository);
    ResponseEntity<?> findByName(String name);
}
