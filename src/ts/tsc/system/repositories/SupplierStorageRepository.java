package ts.tsc.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entities.SupplierStorage;

@Repository
public interface SupplierStorageRepository extends JpaRepository<SupplierStorage, Long> {
}
