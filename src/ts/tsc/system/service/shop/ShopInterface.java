package ts.tsc.system.service.shop;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import ts.tsc.system.entity.purchase.Purchase;
import ts.tsc.system.entity.shop.Shop;
import ts.tsc.system.entity.shop.ShopStorage;
import ts.tsc.system.service.named.NamedService;
import ts.tsc.system.service.order.OrderInterface;

import java.util.List;

public interface ShopInterface extends OrderInterface<Shop> {
    ResponseEntity<?> receiveOrder(Long shopID,
                                   List<Long> productIDList,
                                   List<Integer> countList);
    ResponseEntity<?> transfer(List<Long> productIDList,
                               List<Integer> countList,
                               ShopStorage shopStorage,
                               Purchase purchase, Shop shop);
    ResponseEntity<?> addBudget(Long id, String budgetString);
    ResponseEntity<?> transferProducts(Long shopStorageID,
                                       Long targetShopStorageID,
                                       List<Long> productIDList,
                                       List<Integer> countList);
}
