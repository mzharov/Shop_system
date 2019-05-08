package ts.tsc.system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ts.tsc.system.entities.*;
import ts.tsc.system.repositories.*;


import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Service
public class DBInitializer {

    private final static Logger logger = LoggerFactory.getLogger(DBInitializer.class);
    private final ShopRepository shopRepository;
    private final SupplierRepository supplierRepository;
    private final SupplierStorageRepository supplierStorageRepository;
    private final ProductRepository productRepository;
    private final SupplierStorageProductRepository supplierStorageProductRepository;

    @Autowired
    public DBInitializer(ShopRepository shopRepository,
                         SupplierRepository supplierRepository,
                         SupplierStorageRepository supplierStorageRepository,
                         ProductRepository productRepository,
                         SupplierStorageProductRepository supplierStorageProductRepository) {
        this.shopRepository = shopRepository;
        this.supplierRepository = supplierRepository;
        this.supplierStorageRepository = supplierStorageRepository;
        this.productRepository = productRepository;
        this.supplierStorageProductRepository = supplierStorageProductRepository;
    }

    @PostConstruct
    public void initDB() {
        logger.info("--> Инициализация БД начата");
        addShop("Johan's", 1000);
        addShop("Moran&Johns", 10000);

        Supplier supplier = addSupplier("Avalon");
        Supplier supplier1 = addSupplier("MXK");

        SupplierStorage supplierStorage1= addStorageToSupplier(1000, supplier);
        SupplierStorage supplierStorage2 = addStorageToSupplier(10000, supplier);
        SupplierStorage supplierStorage3 = addStorageToSupplier(100, supplier1);

        Product product1 = addProduct("milk", "drink");
        Product product2 = addProduct("break", "food");
        Product product3 = addProduct("soap", "hygiene care");
        Product product4 = addProduct("apple juice", "drink");

        addProductToStorage(100, new BigDecimal("68.34"),
                new SupplierStorageProductKey(supplierStorage1, product1));
        addProductToStorage(10, new BigDecimal("145.99"),
                new SupplierStorageProductKey(supplierStorage1, product4));

        addProductToStorage(1000, new BigDecimal("23.89"),
                new SupplierStorageProductKey(supplierStorage2, product3));
        addProductToStorage(34, new BigDecimal("12.33"),
                new SupplierStorageProductKey(supplierStorage2, product2));

        addProductToStorage(1, new BigDecimal("34"),
                new SupplierStorageProductKey(supplierStorage3, product3));

        logger.info("--> Инициализация БД завершена");
    }

    private void addProductToStorage(int count, BigDecimal price, SupplierStorageProductKey primaryKey) {
        SupplierStorageProduct supplierStorageProduct = new SupplierStorageProduct();
        supplierStorageProduct.setPrimaryKey(primaryKey);
        supplierStorageProduct.setCount(count);
        supplierStorageProduct.setPrice(price);
        supplierStorageProductRepository.save(supplierStorageProduct);
    }
    private Supplier addSupplier(String name) {
        Supplier supplier = new Supplier();
        supplier.setName("Avalon");
        supplierRepository.save(supplier);
        return supplier;
    }

    private SupplierStorage addStorageToSupplier(int totalSpace, Supplier supplier) {
        SupplierStorage supplierStorage = new SupplierStorage();
        supplierStorage.setSupplier(supplier);
        supplierStorage.setTotalSpace(totalSpace);
        supplierStorage.setFreeSpace(totalSpace);
        supplierStorageRepository.save(supplierStorage);
        return supplierStorage;
    }

    private void addShop(String name, long budget) {
        Shop shop = new Shop();
        shop.setName(name);
        shop.setBudget(budget);
        shopRepository.save(shop);
    }

    private Product addProduct(String name, String category) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        productRepository.save(product);
        return product;
    }

}