package ts.tsc.system.entity.supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.json.serializer.supplier.SupplierStorageProductPrimaryKeySerializer;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@JsonSerialize(using = SupplierStorageProductPrimaryKeySerializer.class)
public class SupplierStorageProductPrimaryKey implements Serializable {
    @ManyToOne
    @JoinColumn(name = "storage_id")
    @JsonIgnore
    private SupplierStorage storage;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    public SupplierStorageProductPrimaryKey() {

    }

    public SupplierStorageProductPrimaryKey(SupplierStorage storage,Product product) {
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
