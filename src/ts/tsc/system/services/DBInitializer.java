package ts.tsc.system.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ts.tsc.system.entities.Shop;
import ts.tsc.system.entities.Supplier;
import ts.tsc.system.entities.SupplierStorage;
import ts.tsc.system.repositories.ShopRepository;
import ts.tsc.system.repositories.SupplierRepository;
import ts.tsc.system.repositories.SupplierStorageRepository;


import javax.annotation.PostConstruct;

@Service
public class DBInitializer {

    private final static Logger logger = LoggerFactory.getLogger(DBInitializer.class);
    private final ShopRepository shopRepository;
    private final SupplierRepository supplierRepository;
    private final SupplierStorageRepository supplierStorageRepository;

    @Autowired
    public DBInitializer(ShopRepository shopRepository,
                         SupplierRepository supplierRepository,
                         SupplierStorageRepository supplierStorageRepository) {
        this.shopRepository = shopRepository;
        this.supplierRepository = supplierRepository;
        this.supplierStorageRepository = supplierStorageRepository;
    }

    @PostConstruct
    public void initDB() {
        logger.info("Starting database initialization...");
        Shop shop1 = new Shop();
        shop1.setName("John");
        shop1.setBudget(1000L);
        shopRepository.save(shop1);

        Shop shop2 = new Shop();
        shop2.setName("Moran");
        shop2.setBudget(10000L);
        shopRepository.save(shop2);

        Supplier supplier = new Supplier();
        supplier.setName("Avalon");
        supplierRepository.save(supplier);

        SupplierStorage supplierStorage = new SupplierStorage();
        supplierStorage.setSupplier(supplier);
        supplierStorage.setTotalSpace(1000);
        supplierStorage.setFreeSpace(1000);
        supplierStorageRepository.save(supplierStorage);
        SupplierStorage supplierStorage2 = new SupplierStorage();
        supplierStorage2.setSupplier(supplier);
        supplierStorage2.setTotalSpace(10000);
        supplierStorage2.setFreeSpace(1000);
        supplierStorageRepository.save(supplierStorage2);

        logger.info("Database initialization finished.");
    }
}