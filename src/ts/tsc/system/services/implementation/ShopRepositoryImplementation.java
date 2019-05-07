package ts.tsc.system.services.implementation;

import ts.tsc.system.entities.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.repositories.ShopRepository;
import ts.tsc.system.services.ShopService;

import java.util.ArrayList;
import java.util.List;

@Service("shopService")
@Transactional
public class ShopRepositoryImplementation implements ShopService {

    @Autowired
    ShopRepository shopRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Shop> findAll() {
        Iterable<Shop> iterable = shopRepository.findAll();
        List<Shop> shops = new ArrayList<>();
        iterable.forEach(shops::add);
        return shops;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shop> findByName(String name) {
        return shopRepository.findByName(name);
    }

    @Override
    public Shop findById(Long id) {
        return shopRepository.findById(id).get();
    }

    @Override
    public Shop save(Shop shop) {
        return shopRepository.save(shop);
    }

    @Override
    public void delete(Shop shop) {
        shopRepository.delete(shop);
    }
}
