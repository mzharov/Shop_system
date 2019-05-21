package ts.tsc.system.service.named;

import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.repository.named.NamedRepository;
import ts.tsc.system.service.base.BaseServiceInterface;

import java.util.List;

public interface NamedServiceInterface<T extends NamedEntity, ID> extends BaseServiceInterface<T, ID> {
    List<T> findByName(String name);
    T update(ID id, T entity);
    NamedRepository<T, ID> getRepository();
}
