package ts.tsc.authentication.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ts.tsc.authentication.entity.User;
import ts.tsc.system.controller.parent.BaseControllerInterface;
import ts.tsc.system.controller.response.BaseResponseBuilder;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.service.named.NamedServiceInterface;

import java.util.Optional;

@RestController
@RequestMapping(value = "/user")
public class UserController implements BaseControllerInterface<User> {

    private final NamedServiceInterface<User, Long> userService;
    private final BaseResponseBuilder<User> userBaseResponseBuilder;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(NamedServiceInterface<User, Long> userService,
                          BaseResponseBuilder<User> userBaseResponseBuilder,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userBaseResponseBuilder = userBaseResponseBuilder;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @GetMapping(value = "/list")
    public ResponseEntity<?> findAll() {
        return userBaseResponseBuilder.getAll(userService.findAll());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<User> deliveryOptional = userService.findById(id);
        return deliveryOptional.<ResponseEntity<?>>map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<?> create(User entity) {
        throw new UnsupportedOperationException();
    }

    @PostMapping(value = "/{username}/{password}")
    public ResponseEntity<?> create(@PathVariable String username, @PathVariable String password) {
        User user = new User();
        user.setName(username);
        user.setPassword(passwordEncoder.encode(password));
        return userBaseResponseBuilder.save(userService.save(user));
    }

    @Override
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User entity) {
        if(entity.getId() !=null) {
            return new ResponseEntity<>(ErrorStatus.ID_CAN_NOT_BE_SET_IN_JSON, HttpStatus.BAD_REQUEST);
        }
        Optional<User> userOptional = userService.findById(id);
        if(userOptional.isPresent()) {
            return userBaseResponseBuilder.save(userService.update(id, userOptional.get()));
        } else {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":user", HttpStatus.NOT_FOUND);
        }
    }
}
