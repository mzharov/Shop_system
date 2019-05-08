package ts.tsc.system.services.interfaces;

import java.util.List;

public interface SupplierService<T> {
    Iterable<T> findAll();
    T findById(Long id);
    T save(T shop);
    void delete(T shop);
}
