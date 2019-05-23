package ts.tsc.system.service.product;

import ts.tsc.system.entity.product.Product;
import ts.tsc.system.service.named.NamedServiceInterface;

import java.util.List;

public interface ProductServiceInterface extends NamedServiceInterface<Product, Long> {
    List<Product> findByCategory(String category);
}
