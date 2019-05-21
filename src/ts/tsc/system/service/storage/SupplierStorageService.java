package ts.tsc.system.service.storage;

import org.springframework.beans.factory.annotation.Autowired;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.entity.supplier.SupplierStorage;
import ts.tsc.system.entity.supplier.SupplierStorageProduct;
import ts.tsc.system.repository.shop.ShopStorageRepository;
import ts.tsc.system.repository.supplier.SupplierStorageRepository;

public class SupplierStorageService
        extends StorageServiceImplementation<Supplier, SupplierStorageProduct, SupplierStorage, Long> {

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
