package ts.tsc.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entities.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long>, NamedRepository<Shop, Long> {
}
