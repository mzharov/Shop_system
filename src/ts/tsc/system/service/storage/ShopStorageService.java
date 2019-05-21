package ts.tsc.system.service.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.entity.shop.ShopStorage;
import ts.tsc.system.entity.shop.ShopStorageProduct;
import ts.tsc.system.repository.shop.ShopStorageRepository;

@Service("shopStorageService")
@Transactional
public class ShopStorageService extends StorageServiceImplementation<Shop, ShopStorageProduct, ShopStorage, Long> {

    private final ShopStorageRepository shopStorageRepository;

    @Autowired
    public ShopStorageService(ShopStorageRepository shopStorageRepository) {
        this.shopStorageRepository = shopStorageRepository;
    }

    @Override
    public ShopStorageRepository getRepository() {
        return this.shopStorageRepository;
    }
}
