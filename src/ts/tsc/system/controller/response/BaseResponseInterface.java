package ts.tsc.system.controller.response;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BaseResponseInterface <T> {
    ResponseEntity<?> getAll(List list);
    ResponseEntity<?> getById(T entity);
    ResponseEntity<?> save(T entity);
}
