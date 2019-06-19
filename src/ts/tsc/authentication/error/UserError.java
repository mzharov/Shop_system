package ts.tsc.authentication.error;

public enum UserError {
    ERROR_WHILE_CHANGING_PASSWORD(-3),
    INVALID_PASSWORD(-2),
    USER_NOT_FOUND(-1),
    SUCCEED(1);

    private final int code;

    UserError(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
