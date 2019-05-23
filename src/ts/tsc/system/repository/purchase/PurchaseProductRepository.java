package ts.tsc.system.repository.purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.purchase.PurchaseProduct;
import ts.tsc.system.entity.purchase.PurchaseProductPrimaryKey;

import java.util.List;

@Repository
public interface PurchaseProductRepository
        extends JpaRepository<PurchaseProduct, PurchaseProductPrimaryKey> {
    @Query("select p.primaryKey.product.id  " +
            "from PurchaseProduct p  where p.primaryKey.purchase.id = ?1")
    List<Long> findPrimaryKeyProductIdByPrimaryKeyPurchaseId(Long purchaseId);
    @Query("select p.count from PurchaseProduct p  where p.primaryKey.purchase.id = ?1")
    List<Integer> findCountByPrimaryKeyPurchaseId(Long purchaseId);
}
