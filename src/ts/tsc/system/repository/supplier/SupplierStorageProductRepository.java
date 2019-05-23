package ts.tsc.system.repository.supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.supplier.SupplierStorageProduct;
import ts.tsc.system.entity.supplier.SupplierStorageProductPrimaryKey;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface SupplierStorageProductRepository
        extends JpaRepository<SupplierStorageProduct, SupplierStorageProductPrimaryKey> {
    Optional<SupplierStorageProduct>
    findByPrimaryKeyStorageIdAndPrimaryKeyProductId(Long storageId, Long productId);

    @Query("select p.count from SupplierStorageProduct p " +
            "where p.primaryKey.storage.id = ?1 " +
            "and p.primaryKey.product.id = ?2")
    Optional<Integer>
    findCountByPrimaryKeyStorageIdAndPrimaryKeyProductId(Long storageId, Long productId);

    @Query("select p.price from SupplierStorageProduct p " +
            "where p.primaryKey.storage.id = ?1 " +
            "and p.primaryKey.product.id  = ?2")
    Optional<BigDecimal>
    findPriceByPrimaryKeyStorageIdAndPrimaryKeyProductId(Long storageId, Long productId);
}
