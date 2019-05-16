package ts.tsc.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.shop.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long>, NamedRepository<Shop, Long> {
}
