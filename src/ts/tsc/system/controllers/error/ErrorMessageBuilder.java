package ts.tsc.system.controllers.error;

import ts.tsc.system.controllers.status.enums.ErrorStatus;

public class ErrorMessageBuilder {
    public static String errorFindAll() {
        return ErrorStatus.ELEMENTS_NOT_FOUND_FOR_ENTITY.toString();
    }
}
