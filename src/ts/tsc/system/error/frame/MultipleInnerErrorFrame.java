package ts.tsc.system.error.frame;

import ts.tsc.system.controller.status.ErrorStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MultipleInnerErrorFrame implements ErrorFrame, Serializable {

    private Map<ErrorStatus, List<String>> errors;

    public MultipleInnerErrorFrame() {
        errors = new HashMap<>();
    }

    public MultipleInnerErrorFrame(Map<ErrorStatus, List<String>> errors) {
        this.errors = errors;
    }

    public Map<ErrorStatus, List<String>> getErrors() {
        return errors;
    }

    public void setErrors(Map<ErrorStatus, List<String>> errors) {
        this.errors = errors;
    }

    public void addSimpleError(ErrorStatus errorStatus, String message) {
        List<String> errorString;
        if(errors.containsKey(errorStatus)) {
            errorString = errors.get(errorStatus);

            if(errorString == null) {
                errorString = new LinkedList<>();
            }
            errorString.add(message);

        } else  {
            errorString = new LinkedList<>();
            errorString.add(message);
        }

        errors.put(errorStatus, errorString);
    }
}
