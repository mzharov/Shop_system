package ts.tsc.system.service.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.repository.named.NamedRepository;
import ts.tsc.system.repository.supplier.SupplierRepository;
import ts.tsc.system.service.named.NamedServiceImplementation;

@Service("supplierService")
@Transactional
public class SupplierService extends NamedServiceImplementation<Supplier, Long> {

    @Autowired
    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public SupplierRepository getRepository() {
        return this.supplierRepository;
    }
}
