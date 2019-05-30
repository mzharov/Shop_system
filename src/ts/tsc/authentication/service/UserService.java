package ts.tsc.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.authentication.entity.User;
import ts.tsc.authentication.repository.UserRepository;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.service.base.BaseService;
import ts.tsc.system.service.base.BaseServiceInterface;
import ts.tsc.system.service.named.NamedService;

@Service
@Transactional
public class UserService extends NamedService<User, Long> {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserRepository getRepository() {
        return this.userRepository;
    }

    @Override
    public User update(Long id, User user) {
        return userRepository.findById(id)
                .map(record -> {
                    record.setName(user.getName());
                    record.setRoles(user.getRoles());
                    return userRepository.save(record);
                }).orElse(null);
    }

}
