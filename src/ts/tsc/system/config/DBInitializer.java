package ts.tsc.system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ts.tsc.system.entities.*;
import ts.tsc.system.entities.keys.ShopStorageProductPrimaryKey;
import ts.tsc.system.entities.keys.SupplierStorageProductPrimaryKey;
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
    private final ShopStorageRepository shopStorageRepository;
    private final ShopStorageProductRepository shopStorageProductRepository;

    @Autowired
    public DBInitializer(ShopRepository shopRepository,
                         SupplierRepository supplierRepository,
                         SupplierStorageRepository supplierStorageRepository,
                         ProductRepository productRepository,
                         SupplierStorageProductRepository supplierStorageProductRepository,
                         ShopStorageRepository shopStorageRepository,
                         ShopStorageProductRepository shopStorageProductRepository) {
        this.shopRepository = shopRepository;
        this.supplierRepository = supplierRepository;
        this.supplierStorageRepository = supplierStorageRepository;
        this.productRepository = productRepository;
        this.supplierStorageProductRepository = supplierStorageProductRepository;
        this.shopStorageRepository = shopStorageRepository;
        this.shopStorageProductRepository = shopStorageProductRepository;
    }

    @PostConstruct
    public void initDB() {
        logger.info("--> Инициализация БД начата");
        Shop shop1 = addShop("Johan's", new BigDecimal("1000000.2356"));
        Shop shop2 = addShop("Moran&Johns", new BigDecimal("100000"));

        Supplier supplier = addSupplier("Avalon");
        Supplier supplier1 = addSupplier("MXK");

        SupplierStorage supplierStorage1= addStorageToSupplier(1000, supplier);
        SupplierStorage supplierStorage2 = addStorageToSupplier(10000, supplier);
        SupplierStorage supplierStorage3 = addStorageToSupplier(100, supplier1);

        Product product1 = addProduct("milk", "drink");
        Product product2 = addProduct("break", "food");
        Product product3 = addProduct("soap", "hygiene care");
        Product product4 = addProduct("apple juice", "drink");

        addProductToSupplierStorage(100, new BigDecimal("68.34"), supplierStorage1, product1);
        addProductToSupplierStorage(10, new BigDecimal("145.99"), supplierStorage1, product4);

        addProductToSupplierStorage(1000, new BigDecimal("23.89"),supplierStorage2, product3);
        addProductToSupplierStorage(34, new BigDecimal("12.33"), supplierStorage2, product2);

        addProductToSupplierStorage(1, new BigDecimal("34"),supplierStorage3, product3);

        ShopStorage shopStorage1 = addStorageToShop(100, 0, shop1);
        ShopStorage shopStorage2 = addStorageToShop(1000, 1, shop1);
        ShopStorage shopStorage3 = addStorageToShop(90, 1, shop2);

        addProductToShopStorage(33, new BigDecimal("68.34"),
                shopStorage1, product1);
        addProductToShopStorage(47, new BigDecimal("168.34"),
                shopStorage1, product2);
        addProductToShopStorage(33, new BigDecimal("68.34"),
                shopStorage2, product1);
        addProductToShopStorage(33, new BigDecimal("68.34"),
                shopStorage3, product1);


        logger.info("--> Инициализация БД завершена");
    }


    private ShopStorage addStorageToShop(int totalSpace, int type, Shop shop) {
        ShopStorage shopStorage = new ShopStorage();
        shopStorage.setShop(shop);
        shopStorage.setType(type);
        shopStorage.setTotalSpace(totalSpace);
        shopStorage.setFreeSpace(totalSpace);
        shopStorageRepository.save(shopStorage);
        return shopStorage;
    }
    private void addProductToShopStorage(int count,
                                         BigDecimal price,
                                         ShopStorage shopStorage,
                                         Product product) {
        ShopStorageProductPrimaryKey productPrimaryKey
                = new ShopStorageProductPrimaryKey(shopStorage, product);
        ShopStorageProduct shopStorageProduct = new ShopStorageProduct();
        shopStorageProduct.setPrimaryKey(productPrimaryKey);
        shopStorageProduct.setCount(count);
        shopStorageProduct.setPrice(price);
        shopStorage.setFreeSpace(shopStorage.getFreeSpace()-count);
        shopStorageRepository.save(shopStorage);
        shopStorageProductRepository.save(shopStorageProduct);
    }

    private void addProductToSupplierStorage(int count,
                                             BigDecimal price,
                                             SupplierStorage supplierStorage,
                                             Product product) {
        SupplierStorageProductPrimaryKey primaryKey
                = new SupplierStorageProductPrimaryKey(supplierStorage, product);
        SupplierStorageProduct supplierStorageProduct = new SupplierStorageProduct();
        supplierStorageProduct.setPrimaryKey(primaryKey);
        supplierStorageProduct.setCount(count);
        supplierStorageProduct.setPrice(price);
        int freeSpace = supplierStorage.getFreeSpace();
        supplierStorage.setFreeSpace(freeSpace-count);
        supplierStorageRepository.save(supplierStorage);
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

    private Shop addShop(String name, BigDecimal budget) {
        Shop shop = new Shop();
        shop.setName(name);
        shop.setBudget(budget);
        shopRepository.save(shop);
        return shop;
    }

    private Product addProduct(String name, String category) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        productRepository.save(product);
        return product;
    }

}