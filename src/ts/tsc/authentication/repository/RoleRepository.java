package ts.tsc.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.authentication.entity.Role;
import ts.tsc.system.repository.named.NamedRepository;

import java.util.Optional;

@Repository
public interface RoleRepository
        extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String name);
 }
