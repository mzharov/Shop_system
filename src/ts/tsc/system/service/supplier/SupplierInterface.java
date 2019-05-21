package ts.tsc.system.service.supplier;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.service.order.OrderInterface;

import java.util.List;

public interface SupplierInterface extends OrderInterface<Supplier> {
    ResponseEntity<?> receiveOrder(Long supplierID,
                                   Long shopStorageID,
                                   List<Long> productIdList,
                                   List<Integer> countList);
    ResponseEntity<?> addProductsToStorage(Long id,
                                           List<Long> productIDList,
                                           List<Integer> countList,
                                           List<String> stringPriceList);
}
