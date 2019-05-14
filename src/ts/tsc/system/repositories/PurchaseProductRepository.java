package ts.tsc.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entities.PurchaseProduct;
import ts.tsc.system.entities.keys.PurchaseProductPrimaryKey;

@Repository
public interface PurchaseProductRepository extends JpaRepository<PurchaseProduct, PurchaseProductPrimaryKey> {
}
