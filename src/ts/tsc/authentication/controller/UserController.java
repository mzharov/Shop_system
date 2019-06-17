package ts.tsc.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ts.tsc.authentication.entity.User;
import ts.tsc.authentication.error.UserError;
import ts.tsc.authentication.service.UserInterface;
import ts.tsc.system.controller.parent.BaseController;
import ts.tsc.system.controller.response.BaseResponseBuilder;
import ts.tsc.system.controller.status.ErrorStatus;

import java.util.Optional;

@RestController
@RequestMapping(value = "/user")
public class UserController extends BaseController<User, UserInterface, Long> {

    private final UserInterface userService;
    private final BaseResponseBuilder<User> userBaseResponseBuilder;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserInterface userService,
                          BaseResponseBuilder<User> userBaseResponseBuilder,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userBaseResponseBuilder = userBaseResponseBuilder;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/{username}/{password}")
    public ResponseEntity<?> create(@PathVariable String username, @PathVariable String password) {
        User user = new User();
        user.setName(username);
        user.setPassword(passwordEncoder.encode(password));

        if(!userService.validateCreatingUser(user)) {
            return new ResponseEntity<>(ErrorStatus.USERNAME_ALREADY_TAKEN, HttpStatus.BAD_REQUEST);
        }

        return userBaseResponseBuilder.save(userService.save(user));
    }

    @PutMapping(value = "/{oldPassword}/{newPassword}")
    public ResponseEntity<?> update(@PathVariable String oldPassword,
                                    @PathVariable String newPassword) {

        Optional<User> userOptional =
                userService.findUserByName(SecurityContextHolder.getContext().getAuthentication().getName());

        if(!userOptional.isPresent()) {
            return new ResponseEntity<>(UserError.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();

        int code = userService.validateUpdatingPassword(user, oldPassword);
        if(code == UserError.INVALID_PASSWORD.getCode()) {
            return new ResponseEntity<>(UserError.INVALID_PASSWORD, HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.revokeToken(user.getName());

        return getResponseBuilder().save(getService().update(user.getId(), user));
    }

    @Override
    protected BaseResponseBuilder<User> getResponseBuilder() {
        return userBaseResponseBuilder;
    }

    @Override
    protected UserInterface getService() {
        return userService;
    }
}
