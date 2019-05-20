package ts.tsc.system.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.shop.ShopStorage;

@Repository
public interface ShopStorageRepository extends JpaRepository<ShopStorage, Long> {
}
