package ts.tsc.system.entity.supplier;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "SupplierStorageProduct")
public class SupplierStorageProduct implements Serializable {

    @EmbeddedId
    private SupplierStorageProductPrimaryKey primaryKey;

    @Column(name = "price", precision = 20, scale = 5)
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
        this.price = new BigDecimal(0).setScale(5, RoundingMode.HALF_UP);
        this.price = this.price.add(price.setScale(5, RoundingMode.HALF_UP));
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
