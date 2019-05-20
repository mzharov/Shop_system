package ts.tsc.system.entity.shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.serializer.shop.ShopStorageProductPrimaryKeySerializer;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@JsonSerialize(using = ShopStorageProductPrimaryKeySerializer.class)
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
