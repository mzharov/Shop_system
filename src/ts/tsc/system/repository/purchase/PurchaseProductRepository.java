package ts.tsc.system.repository.purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.purchase.PurchaseProduct;
import ts.tsc.system.entity.purchase.PurchaseProductPrimaryKey;

@Repository
public interface PurchaseProductRepository extends JpaRepository<PurchaseProduct, PurchaseProductPrimaryKey> {
}
