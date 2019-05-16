package ts.tsc.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.supplier.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>, NamedRepository<Supplier, Long>{
}