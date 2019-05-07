package ts.tsc.system.repositories;

import org.springframework.stereotype.Repository;
import ts.tsc.system.entities.Shop;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface ShopRepository extends CrudRepository<Shop, Long> {
    List<Shop> findByName(String name);
}
