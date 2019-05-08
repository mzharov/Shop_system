package ts.tsc.system.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class SupplierStorageProductKey implements Serializable {
    @ManyToOne
    @JoinColumn(name = "storage_id")
    @JsonIgnore
    private SupplierStorage storage;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    public SupplierStorageProductKey() {

    }

    public SupplierStorageProductKey(SupplierStorage storage,Product product) {
        this.storage = storage;
        this.product = product;
    }

    public SupplierStorage getStorage() {
        return storage;
    }

    public void setStorage(SupplierStorage storage) {
        this.storage = storage;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
