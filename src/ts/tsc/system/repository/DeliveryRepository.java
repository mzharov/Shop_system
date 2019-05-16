package ts.tsc.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.delivery.Delivery;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
