package ts.tsc.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entities.Shop;
import ts.tsc.system.entities.Supplier;
import ts.tsc.system.entities.SupplierStorage;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByName(String name);
}