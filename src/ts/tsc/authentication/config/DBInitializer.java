package ts.tsc.authentication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ts.tsc.authentication.entity.Role;
import ts.tsc.authentication.entity.RoleName;
import ts.tsc.authentication.entity.User;
import ts.tsc.authentication.repository.RoleRepository;
import ts.tsc.authentication.repository.UserRepository;

import javax.annotation.PostConstruct;

@Service("authenticationDBInit")
public class DBInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public DBInitializer(UserRepository userRepository,
                         RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initDB() {

        User user1 = addUser("User1", "password");
        User user2 = addUser("Admin", "admin");

        addRole(RoleName.USER, user1);
        addRole(RoleName.ADMIN, user2);
    }

    private User addUser(String name, String password) {
        User user = new User();
        user.setName(name);
        user.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password));
        userRepository.save(user);
        return user;
    }

    private Role addRole(RoleName name, User user) {
        Role role = new Role();
        role.setRoleName(name);
        role.addUser(user);
        roleRepository.save(role);
        user.addRole(role);
        userRepository.save(user);
        return role;
    }
}
