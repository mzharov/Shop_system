package ts.tsc.system.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.repository.named.NamedRepository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long>, NamedRepository<Shop, Long> {
}
