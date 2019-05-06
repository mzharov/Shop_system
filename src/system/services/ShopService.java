package system.services;

import system.entities.Shop;

import java.util.List;

public interface ShopService {
    Iterable<Shop> findAll();
    List<Shop> findByName(String name);
    Shop findById(Long id);
    Shop save(Shop shop);
    void delete(Shop shop);
}
