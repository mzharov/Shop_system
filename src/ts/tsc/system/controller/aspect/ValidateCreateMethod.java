package ts.tsc.system.controller.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.entity.parent.BaseEntity;

import java.lang.reflect.Method;

/**
 * Прокси для проверки методов создания сущностей,
 * с аннотацией {@link IDValidation} с параметром autoID = true
 * Если в JSON передан параметр id, то будет возвращено
 * сообщение об ошибке - ID_CAN_NOT_BE_SET_IN_JSON
 */
@Aspect
@Component
public class ValidateCreateMethod{
    @Around("((execution(* *.create(*)) && args(entity))" +
            "|| (execution(* *.addStorage(Long, *)) && args(Long, entity)) " +
            "|| (execution(* *.update(Long, *)) && args(Long, entity)))" +
            "&& @annotation(ts.tsc.system.controller.aspect.IDValidation)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, final BaseEntity entity) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        boolean autoID = method.getAnnotation(IDValidation.class).autoID();

        if(autoID && entity.getId()!=null) {
            return new ResponseEntity<>(ErrorStatus.ID_CAN_NOT_BE_SET_IN_JSON, HttpStatus.BAD_REQUEST);
        }

        return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    }
}
