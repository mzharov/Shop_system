package ts.tsc.system.service.storage.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.repository.supplier.SupplierStorageProductRepository;
import ts.tsc.system.service.base.BaseServiceImplementation;

@Service("supplierStorageProductService")
@Transactional
public class SupplierStorageProductService extends BaseServiceImplementation {

    private final SupplierStorageProductRepository supplierStorageProductRepository;

    @Autowired
    SupplierStorageProductService(SupplierStorageProductRepository supplierStorageProductRepository) {
        this.supplierStorageProductRepository = supplierStorageProductRepository;
    }

    @Override
    public SupplierStorageProductRepository getRepository() {
        return this.supplierStorageProductRepository;
    }
}
