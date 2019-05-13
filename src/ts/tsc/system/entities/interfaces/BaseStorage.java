package ts.tsc.system.entities.interfaces;

import javax.persistence.MappedSuperclass;

public interface BaseStorage<T>{
    void setOwner(T entity);
}
