package ts.tsc.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entities.Delivery;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
