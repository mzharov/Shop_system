package ts.tsc.system.error.frame;

import ts.tsc.system.controller.status.ErrorStatus;

import java.io.Serializable;
import java.util.Map;

public class MultipleErrorFrame implements ErrorFrame, Serializable {
    private Map<ErrorStatus, String> errors;

    public MultipleErrorFrame(Map<ErrorStatus, String> errors) {
        this.errors = errors;
    }

    public Map<ErrorStatus, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<ErrorStatus, String> errors) {
        this.errors = errors;
    }
}
