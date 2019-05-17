package ts.tsc.system.entity.parent;

import ts.tsc.system.entity.product.Product;
import ts.tsc.system.entity.shop.ShopStorageProduct;

import java.util.Set;

public interface BaseStorage<T, P>{
    void setOwner(T entity);
    Set<P> getProducts();
}
