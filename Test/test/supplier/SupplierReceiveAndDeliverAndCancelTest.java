package test.supplier;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Тестирование процесса поступления заказа, его доставки и отмены
 */
public class SupplierReceiveAndDeliverAndCancelTest extends SupplierOrderTest{
    private final static Logger logger
            = LoggerFactory.getLogger(SupplierReceiveAndDeliverAndCancelTest.class);

    @Test
    @SuppressWarnings("unchecked")
    public void receiveAndDeliverAndCompleteOrder() {
        logger.info("Поступление заказа, доставка и отмена заказа");
        Long supplierStorageID = 1L;
        Long shopStorageID = 3L;
        List<Long> productIDList = Arrays.asList(1L, 4L);
        List<Integer> countList = Arrays.asList(46, 10);
        Object[] parameters
                = receiveOrder(supplierStorageID, shopStorageID, productIDList, countList);

        Long deliveryID = (Long)parameters[0];
        int supplierPreviousSpace = (Integer) parameters[1];
        int shopPreviousSpace = (Integer) parameters[2];
        BigDecimal shopPreviousBudget = (BigDecimal) parameters[5];
        List<Integer> supplierPreviousCount = (List<Integer>) parameters[6];
        List<Integer> shopPreviousCount = (List<Integer>) parameters[7];
        deliverOrder(deliveryID);

        cancelOrder(deliveryID,
                supplierPreviousSpace,
                shopPreviousSpace,
                shopPreviousBudget,
                supplierPreviousCount,
                shopPreviousCount);
    }
}
