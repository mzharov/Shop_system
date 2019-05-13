package ts.tsc.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entities.Shop;
import ts.tsc.system.entities.Supplier;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>, NamedRepository<Supplier, Long>{
}