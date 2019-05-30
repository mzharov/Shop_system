package ts.tsc.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import ts.tsc.authentication.entity.User;
import ts.tsc.system.repository.named.NamedRepository;

@Repository
public interface UserRepository
        extends NamedRepository<User, Long> {
}
