package ts.tsc.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.shop.ShopStorageProduct;
import ts.tsc.system.entity.shop.ShopStorageProductPrimaryKey;

@Repository
public interface ShopStorageProductRepository extends JpaRepository<ShopStorageProduct, ShopStorageProductPrimaryKey> {
}
