package ts.tsc.system.entity.parent;

import java.util.Set;

public interface BaseStorage<T, P>{
    void setOwner(T entity);
    Set<P> getProducts();
}
