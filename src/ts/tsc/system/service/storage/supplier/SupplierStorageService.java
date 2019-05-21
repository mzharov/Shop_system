package ts.tsc.system.service.storage.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.entity.supplier.SupplierStorage;
import ts.tsc.system.entity.supplier.SupplierStorageProduct;
import ts.tsc.system.repository.supplier.SupplierStorageRepository;
import ts.tsc.system.service.storage.manager.StorageServiceManager;

@Service("supplierStorageService")
@Transactional
public class SupplierStorageService
        extends StorageServiceManager<Supplier, SupplierStorageProduct, SupplierStorage, Long> {

    private final SupplierStorageRepository supplierStorageRepository;

    @Autowired
    public SupplierStorageService(SupplierStorageRepository supplierStorageRepository) {
        this.supplierStorageRepository = supplierStorageRepository;
    }

    @Override
    public SupplierStorageRepository getRepository() {
        return this.supplierStorageRepository;
    }
}
