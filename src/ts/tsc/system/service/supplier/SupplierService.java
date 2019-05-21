package ts.tsc.system.service.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.repository.supplier.SupplierRepository;
import ts.tsc.system.service.named.NamedServiceImplementation;

@Service("supplierService")
@Transactional
public class SupplierService extends NamedServiceImplementation<Supplier, Long> {

    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public SupplierRepository getRepository() {
        return this.supplierRepository;
    }

    @Override
    public Supplier update(Long id, Supplier supplier) {
        return supplierRepository.findById(id)
                .map(record -> {
                    record.setName(supplier.getName());
                    record.setStorages(supplier.getStorages());
                    return supplierRepository.save(record);
                }).orElse(null);
    }
}
