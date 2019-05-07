package ts.tsc.system.services.interfaces;

import java.util.List;

public interface EntityService<T> {
    Iterable<T> findAll();
    List<T> findByName(String name);
    T findById(Long id);
    T save(T shop);
    void delete(T shop);
}
