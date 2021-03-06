package ts.tsc.authentication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DBInitializer(UserRepository userRepository,
                         RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initDB() {

        User user1 = addUser("Admin", "admin");
        User user2 = addUser("User1", "password");

        addRole(RoleName.ADMIN, user1);
        addRole(RoleName.USER, user2);
    }

    private User addUser(String name, String password) {
        User user = new User();
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    private void addRole(RoleName name, User user) {
        Role role = new Role();
        role.setRoleName(name);
        role.addUser(user);
        roleRepository.save(role);
        user.addRole(role);
        userRepository.save(user);
    }
}
