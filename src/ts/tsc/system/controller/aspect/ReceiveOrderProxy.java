package ts.tsc.system.controller.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.entity.delivery.Delivery;
import ts.tsc.system.entity.purchase.Purchase;
import ts.tsc.system.repository.delivery.DeliveryRepository;
import ts.tsc.system.repository.purchase.PurchaseRepository;

/**
 * Прокси для обработки возвращаемого значения
 * процесса заказов
 */
@Aspect
@Component
@Transactional
public class ReceiveOrderProxy {

    private final PurchaseRepository purchaseRepository;
    private final DeliveryRepository deliveryRepository;

    @Autowired
    ReceiveOrderProxy(PurchaseRepository purchaseRepository,
                      DeliveryRepository deliveryRepository) {
        this.purchaseRepository = purchaseRepository;
        this.deliveryRepository = deliveryRepository;
    }

    @Around("execution(* *.receiveOrder(*,*,*))")
    public ResponseEntity<?> receivePurchase(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        ResponseEntity<?> responseEntity = (ResponseEntity<?>)
                proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        if(responseEntity.getBody() instanceof Purchase) {
            Purchase purchase = (Purchase) responseEntity.getBody();
            return purchaseRepository.findById(purchase.getId())
                    .<ResponseEntity<?>>map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
        }
        return responseEntity;
    }

    @Around("execution(* *.receiveOrder(*,*,*,*))")
    public ResponseEntity<?> receiveDelivery(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        ResponseEntity<?> responseEntity = (ResponseEntity<?>)
                proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        if(responseEntity.getBody() instanceof Delivery) {
            Delivery delivery = (Delivery) responseEntity.getBody();
            return deliveryRepository.findById(delivery.getId())
                    .<ResponseEntity<?>>map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
        }
        return responseEntity;
    }
}
