package ts.tsc.system.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.shop.ShopStorage;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopStorageRepository extends JpaRepository<ShopStorage, Long> {
    Optional<ShopStorage> findByShopIdAndType(Long shopID, int type);
    @Query("select entity from ShopStorage entity where entity.shop.id = ?1")
    List<ShopStorage> findByOwnerId(Long id);
}
