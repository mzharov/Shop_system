package ts.tsc.system.repository.named;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ts.tsc.system.entity.parent.NamedEntity;

import java.util.List;

@NoRepositoryBean
public interface NamedRepository<T extends NamedEntity,I> extends JpaRepository<T, I> {
    List<T> findByName(String name);
}
