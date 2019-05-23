package ts.tsc.system.repository.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.delivery.DeliveryProduct;
import ts.tsc.system.entity.delivery.DeliveryProductPrimaryKey;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryProductRepository
        extends JpaRepository<DeliveryProduct, DeliveryProductPrimaryKey> {
    @Query("select sum(p.count) from DeliveryProduct p " +
            "where p.primaryKey.delivery.id = ?1")
    Optional<Integer> findSumOfCountByPrimaryKeyDeliveryId(Long deliveryId);

    List<DeliveryProduct> findByPrimaryKeyDeliveryId(Long deliveryId);

    @Query("select p.primaryKey.product.id  " +
            "from DeliveryProduct p  where p.primaryKey.delivery.id = ?1")
    List<Long> findPrimaryKeyProductIdByPrimaryKeyDeliveryId(Long purchaseId);

    @Query("select p.count from DeliveryProduct p  where p.primaryKey.delivery.id = ?1")
    List<Integer> findCountByPrimaryKeyDeliveryId(Long purchaseId);
}
