package ts.tsc.system.entities;

import ts.tsc.system.entities.keys.PurchaseProductPrimaryKey;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "purchaseProduct")
public class PurchaseProduct implements Serializable {
    @EmbeddedId
    private PurchaseProductPrimaryKey primaryKey;

    @Column(name = "count")
    private int count;

    @Column(name = "price")
    private BigDecimal price;

    public PurchaseProductPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(PurchaseProductPrimaryKey primaryKey) {
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
