package ts.tsc.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ts.tsc.authentication.entity.User;
import ts.tsc.system.controller.parent.BaseControllerInterface;
import ts.tsc.system.controller.response.BaseResponseBuilder;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.service.base.BaseServiceInterface;
import ts.tsc.system.service.named.NamedServiceInterface;

import java.util.Optional;

@RestController
@RequestMapping(value = "/user")
public class UserController implements BaseControllerInterface<User> {

    private final NamedServiceInterface<User, Long> userService;
    private final BaseResponseBuilder<User> userBaseResponseBuilder;

    @Autowired
    public UserController(NamedServiceInterface<User, Long> userService,
                          BaseResponseBuilder<User> userBaseResponseBuilder) {
        this.userService = userService;
        this.userBaseResponseBuilder = userBaseResponseBuilder;
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
    @PostMapping(value = "/")
    public ResponseEntity<?> create(@RequestBody User entity) {
        if(entity.getId() !=null) {
            return new ResponseEntity<>(ErrorStatus.ID_CAN_NOT_BE_SET_IN_JSON, HttpStatus.BAD_REQUEST);
        }
        return userBaseResponseBuilder.save(userService.save(entity));
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
