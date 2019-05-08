package ts.tsc.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entities.ShopStorageProduct;
import ts.tsc.system.entities.ShopStorageProductPrimaryKey;

@Repository
public interface ShopStorageProductRepository extends JpaRepository<ShopStorageProduct, ShopStorageProductPrimaryKey> {
}
