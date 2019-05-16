package ts.tsc.system.controllers.parent;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import ts.tsc.system.controllers.status.enums.ErrorStatus;
import ts.tsc.system.controllers.status.enums.Status;

public abstract class OrderController {
    protected abstract ResponseEntity<?> deliverOrder(Long id);
    protected abstract ResponseEntity<?> completeOrder(Long id);
    protected abstract ResponseEntity<?> cancelOrder(Long id);
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @PathVariable Status status) {
        if(status.equals(Status.DELIVERING)) {
            return deliverOrder(id);
        }
        if(status.equals(Status.COMPLETED)) {
            return completeOrder(id);
        }
        if(status.equals(Status.CANCELED)) {
            return cancelOrder(id);
        }
        return new ResponseEntity<>(ErrorStatus.UNKNOWN_DELIVER_STATUS, HttpStatus.BAD_REQUEST);
    }
}
