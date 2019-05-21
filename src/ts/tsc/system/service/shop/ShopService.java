package ts.tsc.system.service.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.repository.shop.ShopRepository;
import ts.tsc.system.service.named.NamedServiceImplementation;
import ts.tsc.system.service.order.ShopInterface;

import java.util.List;

@Service("shopService")
@Transactional
public class ShopService extends NamedServiceImplementation<Shop, Long> implements ShopInterface {


    private final ShopRepository shopRepository;

    @Autowired
    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    @Override
    public ShopRepository getRepository() {
        return this.shopRepository;
    }

    @Override
    public Shop update(Long id, Shop shop) {
        return shopRepository.findById(id)
                .map(record -> {
                    record.setName(shop.getName());
                    record.setBudget(shop.getBudget());
                    record.setPurchases(record.getPurchases());
                    record.setStorages(record.getStorages());
                    return shopRepository.save(record);
                }).orElse(null);
    }

    @Override
    public ResponseEntity<?> deliverOrder(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<?> completeOrder(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<?> cancelOrder(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<?> receiveOrder(Long shopID, List<Long> productID, List<Integer> count) {
        return null;
    }
}
