package ts.tsc.system.repository.product;

import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.repository.named.NamedRepository;

import java.util.List;

@Repository
public interface ProductRepository extends NamedRepository<Product, Long> {
    List<Product> getByCategory(String category);
 }
