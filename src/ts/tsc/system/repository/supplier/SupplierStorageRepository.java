package ts.tsc.system.repository.supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.supplier.SupplierStorage;

@Repository
public interface SupplierStorageRepository extends JpaRepository<SupplierStorage, Long> {
}
