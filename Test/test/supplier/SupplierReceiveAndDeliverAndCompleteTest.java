package test.supplier;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Тестирование поступления заказа, доставки и его завершения
 */
public class SupplierReceiveAndDeliverAndCompleteTest extends SupplierOrderTest {

    private final static Logger logger
            = LoggerFactory.getLogger(SupplierReceiveAndDeliverAndCompleteTest.class);

    @Test
    public void receiveAndDeliverAndCompleteOrder() {
        logger.info("Заказ, доставка и завершение заказа");
        Long supplierStorageID = 1L;
        Long shopStorageID = 3L;
        List<Long> productIDList = Arrays.asList(1L, 4L);
        List<Integer> countList = Arrays.asList(46, 10);
        Object[] parameters
                = receiveOrder(supplierStorageID, shopStorageID, productIDList, countList);
        Long deliveryID = (Long)parameters[0];
        int productCountSum = (Integer) parameters[3];
        BigDecimal productPriceSum = (BigDecimal) parameters[4];
        BigDecimal shopPreviousBudget = (BigDecimal) parameters[5];
        deliverOrder(deliveryID);
        completeOrder(deliveryID,
                shopStorageID,
                productCountSum,
                productPriceSum,
                shopPreviousBudget);
    }
}
