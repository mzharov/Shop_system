package ts.tsc.system.controllers.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ts.tsc.system.entities.Shop;
import ts.tsc.system.entities.ShopStorage;
import ts.tsc.system.entities.ShopStorageProduct;

import java.util.List;

public interface ExtendedControllerInterface<T, S, P> extends BaseControllerInterface<T>{
    ResponseEntity<List<S>> findStorageById(Long id);
    ResponseEntity<List<S>> findAllStorages();
    ResponseEntity<?> addStorage(Long id, S storage);
    ResponseEntity<List<P>> getProducts();
}
