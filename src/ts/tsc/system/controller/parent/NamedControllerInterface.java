package ts.tsc.system.controller.parent;

import org.springframework.http.ResponseEntity;

public interface NamedControllerInterface<T>
        extends BaseControllerInterface<T> {
    ResponseEntity<?> findByName(String name);
}
