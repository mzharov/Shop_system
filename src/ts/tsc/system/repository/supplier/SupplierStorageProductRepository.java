package ts.tsc.system.repository.supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.supplier.SupplierStorageProduct;
import ts.tsc.system.entity.supplier.SupplierStorageProductPrimaryKey;

@Repository
public interface SupplierStorageProductRepository
        extends JpaRepository<SupplierStorageProduct, SupplierStorageProductPrimaryKey> {
}
