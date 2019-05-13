package ts.tsc.system.entities;

import ts.tsc.system.entities.keys.DeliveryProductPrimaryKey;
import ts.tsc.system.entities.keys.ShopStorageProductPrimaryKey;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "deliveryProduct")
public class DeliveryProduct implements Serializable {
    @EmbeddedId
    private DeliveryProductPrimaryKey primaryKey;

    @Column(name = "count")
    private int count;

    @Column(name="price")
    private BigDecimal price;

    public DeliveryProductPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(DeliveryProductPrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
