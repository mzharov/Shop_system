package ts.tsc.system.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ts.tsc.system.entities.Shop;
import ts.tsc.system.entities.Supplier;
import ts.tsc.system.repositories.ShopRepository;
import ts.tsc.system.repositories.SupplierRepository;


import javax.annotation.PostConstruct;

@Service
public class DBInitializer {

    private final static Logger logger = LoggerFactory.getLogger(DBInitializer.class);
    private final ShopRepository shopRepository;
    private final SupplierRepository repository;

    @Autowired
    public DBInitializer(ShopRepository shopRepository, SupplierRepository repository) {
        this.shopRepository = shopRepository;
        this.repository = repository;
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
        repository.save(supplier);

        logger.info("Database initialization finished.");
    }
}