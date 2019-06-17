package ts.tsc.system.controller.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ts.tsc.system.controller.status.ErrorStatus;

import java.util.List;

@Component("baseResponseBuilder")
public class BaseResponseBuilder<T> {
    public ResponseEntity<?> getAll(List list) {
        if (list.size() > 0) {
            return ResponseEntity.ok().body(list);
        } else {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> save(T entity) {
        if(entity !=null) {
            return new ResponseEntity<>(entity, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
