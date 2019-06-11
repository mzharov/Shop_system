package ts.tsc.system.error.exception;

import ts.tsc.system.error.frame.ErrorFrame;

public class NotFoundException extends RuntimeException {
    private final ErrorFrame errorFrame;

    public NotFoundException(ErrorFrame errorFrame) {
        this.errorFrame = errorFrame;
    }

    public ErrorFrame getErrorFrame() {
        return errorFrame;
    }
}
