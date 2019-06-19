package ts.tsc.authentication.service;

import ts.tsc.authentication.entity.User;
import ts.tsc.system.service.named.NamedServiceInterface;

import java.util.Optional;

public interface UserInterface extends NamedServiceInterface<User, Long> {
    User save(User user);
    boolean validateCreatingUser(User user);
    int validateUpdatingPassword(User user, String oldPassword);
    Optional<User> findUserByName(String username);
    int revokeToken(String username);
}
