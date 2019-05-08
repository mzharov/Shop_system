package ts.tsc.system.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class ShopStorageProductPrimaryKey implements Serializable {
    @ManyToOne
    @JoinColumn(name = "storage_id")
    @JsonIgnore
    private ShopStorage storage;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    public ShopStorageProductPrimaryKey() {

    }
    public ShopStorageProductPrimaryKey(ShopStorage shopStorage, Product product) {
        this.storage = shopStorage;
        this.product = product;
    }

    public ShopStorage getStorage() {
        return storage;
    }

    public void setStorage(ShopStorage storage) {
        this.storage = storage;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
