package ts.tsc.system.error.frame;

import java.io.Serializable;

class SingleError implements ErrorFrame, Serializable {
    private String error;

    public SingleError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
