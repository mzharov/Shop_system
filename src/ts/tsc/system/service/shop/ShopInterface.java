package ts.tsc.system.service.shop;

import org.springframework.http.ResponseEntity;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.service.order.OrderInterface;

import java.util.List;

public interface ShopInterface extends OrderInterface<Shop, Long> {
    ResponseEntity<?> receiveOrder(Long shopID,
                                   List<Long> productIDList,
                                   List<Integer> countList);
    ResponseEntity<?> addBudget(Long id, String budgetString);
    ResponseEntity<?> transferProducts(Long shopStorageID,
                                       Long targetShopStorageID,
                                       List<Long> productIDList,
                                       List<Integer> countList);
}
