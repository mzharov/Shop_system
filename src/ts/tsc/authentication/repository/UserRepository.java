package ts.tsc.authentication.repository;

import org.springframework.stereotype.Repository;
import ts.tsc.authentication.entity.User;
import ts.tsc.system.repository.named.NamedRepository;

import java.util.Optional;

@Repository
public interface UserRepository
        extends NamedRepository<User, Long> {
    Optional<User> findUserByName(String name);
}
