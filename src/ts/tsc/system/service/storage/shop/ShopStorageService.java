package ts.tsc.system.service.storage.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.entity.shop.ShopStorage;
import ts.tsc.system.entity.shop.ShopStorageProduct;
import ts.tsc.system.repository.shop.ShopStorageRepository;
import ts.tsc.system.service.named.NamedServiceInterface;
import ts.tsc.system.service.storage.manager.StorageServiceManager;

import java.util.Optional;

@Service("shopStorageService")
@Transactional
public class ShopStorageService
        extends StorageServiceManager<Shop, ShopStorageProduct, ShopStorage, Long> {

    private final ShopStorageRepository shopStorageRepository;

    @Autowired
    public ShopStorageService(ShopStorageRepository shopStorageRepository) {
        this.shopStorageRepository = shopStorageRepository;
    }

    @Override
    public ShopStorageRepository getRepository() {
        return this.shopStorageRepository;
    }

    @Override
    public ResponseEntity<?> addStorage(Long shopID,
                                        ShopStorage storage,
                                        NamedServiceInterface<Shop, Long> namedService) {

        Optional<ShopStorage> shopStorageOptional
                = shopStorageRepository.findByShopIdAndType(shopID, 1);
        if(shopStorageOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.MAIN_STORAGE_ALREADY_EXISTS,
                    HttpStatus.BAD_REQUEST);
        }
        return super.addStorage(shopID, storage, namedService);
    }
}
