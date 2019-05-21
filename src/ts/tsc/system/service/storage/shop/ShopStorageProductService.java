package ts.tsc.system.service.storage.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.repository.shop.ShopStorageProductRepository;
import ts.tsc.system.service.base.BaseService;

@Service("shopStorageProductService")
@Transactional
public class ShopStorageProductService extends BaseService {

    private final ShopStorageProductRepository shopStorageProductRepository;

    @Autowired
    public ShopStorageProductService(ShopStorageProductRepository shopStorageProductRepository) {
        this.shopStorageProductRepository = shopStorageProductRepository;
    }

    @Override
    public ShopStorageProductRepository getRepository() {
        return this.shopStorageProductRepository;
    }
}
