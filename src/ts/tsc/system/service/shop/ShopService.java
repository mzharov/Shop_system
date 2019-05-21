package ts.tsc.system.service.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.repository.named.NamedRepository;
import ts.tsc.system.repository.shop.ShopRepository;
import ts.tsc.system.service.named.NamedServiceImplementation;

@Service("shopService")
@Transactional
public class ShopService extends NamedServiceImplementation<Shop, Long> {


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
                    return shopRepository.save(record);
                }).orElse(null);
    }
}
