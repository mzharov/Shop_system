package ts.tsc.system.entities;

import javax.persistence.MappedSuperclass;

public interface BaseStorage<T>{
    void setOwner(T entity);
}
