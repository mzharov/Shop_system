package ts.tsc.system.repository.supplier;

import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.repository.named.NamedRepository;

@Repository
public interface SupplierRepository extends NamedRepository<Supplier, Long> {
}