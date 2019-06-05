package ts.tsc.authentication.service;

import ts.tsc.authentication.entity.User;
import ts.tsc.system.service.named.NamedServiceInterface;

public interface UserInterface extends NamedServiceInterface<User, Long> {
    User save(User user);
    boolean validateUser(User user);
}
