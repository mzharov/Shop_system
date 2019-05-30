package ts.tsc.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ts.tsc.authentication.entity.Role;

@Repository
public interface RoleRepository
        extends JpaRepository<Role, Long> {
}
