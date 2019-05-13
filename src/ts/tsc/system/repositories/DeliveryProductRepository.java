package ts.tsc.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entities.DeliveryProduct;
import ts.tsc.system.entities.keys.DeliveryProductPrimaryKey;

@Repository
public interface DeliveryProductRepository
        extends JpaRepository<DeliveryProduct, DeliveryProductPrimaryKey> {
}
