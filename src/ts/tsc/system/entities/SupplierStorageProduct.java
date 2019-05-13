package ts.tsc.system.entities;

import ts.tsc.system.entities.keys.SupplierStorageProductPrimaryKey;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "SupplierStorageProduct")
public class SupplierStorageProduct implements Serializable {

    @EmbeddedId
    private SupplierStorageProductPrimaryKey primaryKey;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "count")
    private int count;

    public SupplierStorageProductPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(SupplierStorageProductPrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
