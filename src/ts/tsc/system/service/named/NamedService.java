package ts.tsc.system.service.named;

import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.service.base.BaseService;

import java.util.List;

public interface NamedService<T extends NamedEntity, ID> extends BaseService<T, ID> {
    List<T> findByName(String name);
    T update(ID id, T entity);
}
