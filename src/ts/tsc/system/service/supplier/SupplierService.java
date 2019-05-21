package ts.tsc.system.service.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.repository.supplier.SupplierRepository;
import ts.tsc.system.service.named.NamedServiceImplementation;
import ts.tsc.system.service.order.SupplierInterface;

@Service("supplierService")
@Transactional
public class SupplierService extends NamedServiceImplementation<Supplier, Long> implements SupplierInterface {

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

    @Override
    public ResponseEntity<?> deliverOrder(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<?> completeOrder(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<?> cancelOrder(Long id) {
        return null;
    }

}
