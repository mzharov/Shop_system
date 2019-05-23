package ts.tsc.system.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.shop.ShopStorageProduct;
import ts.tsc.system.entity.shop.ShopStorageProductPrimaryKey;

import java.util.Optional;

@Repository
public interface ShopStorageProductRepository
        extends JpaRepository<ShopStorageProduct, ShopStorageProductPrimaryKey> {
    @Query("select p.count from ShopStorageProduct p " +
            "where p.primaryKey.storage.id = ?1 " +
            "and p.primaryKey.product.id =?2")
    Optional<Integer> findCountByPrimaryKeyStorageIdAndPrimaryKeyProductId(Long storageID, Long productID);
    Optional<ShopStorageProduct> findByPrimaryKeyStorageIdAndPrimaryKeyProductId(Long storageID, Long productID);
}
