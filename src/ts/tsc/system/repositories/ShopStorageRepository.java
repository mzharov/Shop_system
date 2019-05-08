package ts.tsc.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entities.ShopStorage;

@Repository
public interface ShopStorageRepository extends JpaRepository<ShopStorage, Long> {
}
