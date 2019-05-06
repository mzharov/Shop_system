package system.repositories;

import system.entities.Shop;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ShopRepository extends CrudRepository<Shop, Long> {
    List<Shop> findByName(String name);
}
