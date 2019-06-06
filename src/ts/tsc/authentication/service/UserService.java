package ts.tsc.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.authentication.entity.Role;
import ts.tsc.authentication.entity.RoleName;
import ts.tsc.authentication.entity.User;
import ts.tsc.authentication.repository.RoleRepository;
import ts.tsc.authentication.repository.UserRepository;
import ts.tsc.system.service.named.NamedService;

import java.util.Optional;

@Service("userService")
@Transactional
public class UserService extends NamedService<User, Long> implements UserInterface {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserRepository getRepository() {
        return this.userRepository;
    }

    public User update(Long id, User user) {
        return userRepository.findById(id)
                .map(record -> {
                    record.setName(user.getName());
                    record.setRoles(user.getRoles());
                    return userRepository.save(record);
                }).orElse(null);
    }

    @Override
    public boolean validateUser(User user) {
        Optional<User> userOptional = userRepository.findUserByName(user.getName());
        return !userOptional.isPresent();
    }

    @Override
    public User save(User user) {

        Optional<Role> roleOptional
                = roleRepository.findByRoleName(RoleName.USER.toString());
        if(!roleOptional.isPresent()) {
            return null;
        }
        try {
            Role role = roleOptional.get();
            role.addUser(user);
            roleRepository.save(role);
        } catch (Exception e) {
            return null;
        }
        user.addRole(roleOptional.get());
        return super.save(user);
    }
}
