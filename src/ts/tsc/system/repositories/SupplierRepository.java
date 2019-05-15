package ts.tsc.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entities.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>, NamedRepository<Supplier, Long>{
}