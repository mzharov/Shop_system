package ts.tsc.system.controller.parent;

import org.springframework.http.ResponseEntity;

public interface NamedControllerInterface<T, ID>
        extends BaseControllerInterface<T,ID> {
    ResponseEntity<?> findByName(String name);
}
