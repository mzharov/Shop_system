package ts.tsc.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entities.Supplier;
import ts.tsc.system.entities.SupplierStorage;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}