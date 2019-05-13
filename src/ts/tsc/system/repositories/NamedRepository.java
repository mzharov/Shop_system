package ts.tsc.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ts.tsc.system.entities.NamedEntity;
import ts.tsc.system.entities.Shop;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface NamedRepository<T extends NamedEntity,I> extends JpaRepository<T, I> {
    Optional<List<T>> findByName(String name);
}
