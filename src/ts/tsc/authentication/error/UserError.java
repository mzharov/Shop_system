package ts.tsc.authentication.error;

public enum UserError {
    INVALID_PASSWORD(-2),
    USER_NOT_FOUND(-1),
    VALID_USER(1);

    private int code;

    UserError(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
