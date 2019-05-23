package ts.tsc.system.repository.shop;

import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.repository.named.NamedRepository;

@Repository
public interface ShopRepository extends NamedRepository<Shop, Long> {
}
