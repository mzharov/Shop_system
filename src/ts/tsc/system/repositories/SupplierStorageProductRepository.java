package ts.tsc.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entities.SupplierStorageProduct;
import ts.tsc.system.entities.SupplierStorageProductKey;

@Repository
public interface SupplierStorageProductRepository
        extends JpaRepository<SupplierStorageProduct, SupplierStorageProductKey> {
}
