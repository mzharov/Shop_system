package ts.tsc.system.repository.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.delivery.DeliveryProduct;
import ts.tsc.system.entity.delivery.DeliveryProductPrimaryKey;

@Repository
public interface DeliveryProductRepository
        extends JpaRepository<DeliveryProduct, DeliveryProductPrimaryKey> {
}
