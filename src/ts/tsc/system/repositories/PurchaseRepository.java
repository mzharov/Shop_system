package ts.tsc.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entities.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
