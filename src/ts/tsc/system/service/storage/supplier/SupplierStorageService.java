package ts.tsc.system.service.storage.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.entity.supplier.SupplierStorage;
import ts.tsc.system.entity.supplier.SupplierStorageProduct;
import ts.tsc.system.repository.named.NamedRepository;
import ts.tsc.system.repository.supplier.SupplierRepository;
import ts.tsc.system.repository.supplier.SupplierStorageRepository;
import ts.tsc.system.service.storage.manager.StorageServiceManager;

import java.util.List;

@Service("supplierStorageService")
@Transactional
public class SupplierStorageService
        extends StorageServiceManager<Supplier, SupplierStorageProduct, SupplierStorage, Long> {

    private final SupplierStorageRepository supplierStorageRepository;
    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierStorageService(SupplierStorageRepository supplierStorageRepository,
                                  SupplierRepository supplierRepository) {
        this.supplierStorageRepository = supplierStorageRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public SupplierStorageRepository getRepository() {
        return this.supplierStorageRepository;
    }

    @Override
    public List<SupplierStorage> findStoragesByOwnerId(Long id) {
        return supplierStorageRepository.findByOwnerId(id);
    }

    @Override
    public NamedRepository<Supplier, Long> getOwnerService() {
        return supplierRepository;
    }
}
