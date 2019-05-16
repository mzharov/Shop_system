package ts.tsc.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.system.entity.product.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, NamedRepository<Product, Long> {
    Optional<List<Product>> getByName(String name);
    Optional<List<Product>> getByCategory(String category);
 }
