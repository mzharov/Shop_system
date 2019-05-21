package ts.tsc.system.controller.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ts.tsc.system.controller.status.ErrorStatus;

import javax.xml.ws.Response;
import java.util.List;
import java.util.Optional;

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
            return ResponseEntity.ok().body(entity);
        } else {
            return ResponseEntity.unprocessableEntity().body(ErrorStatus.ERROR_WHILE_SAVING);
        }
    }
}
