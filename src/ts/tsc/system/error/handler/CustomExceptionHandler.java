package ts.tsc.system.error.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ts.tsc.system.error.exception.NotFoundException;
import ts.tsc.system.error.frame.ErrorFrame;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorFrame> handleNotFoundException(NotFoundException exception) {
        return new ResponseEntity<>(exception.getErrorFrame(), HttpStatus.NOT_FOUND);
    }
}
