package ts.tsc.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.authentication.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
