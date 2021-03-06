package ts.tsc.system.entity.purchase;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "purchaseProduct")
public class PurchaseProduct implements Serializable {
    @EmbeddedId
    private PurchaseProductPrimaryKey primaryKey;

    @Column(name = "count")
    private int count;

    @Column(name = "price", precision = 20, scale = 5)
    private BigDecimal price;

    @Column(name = "sumPrice", precision = 20, scale = 5)
    private BigDecimal sumPrice;

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
        this.price = new BigDecimal(0).setScale(5, RoundingMode.HALF_UP);
        this.price = this.price.add(price.setScale(5, RoundingMode.HALF_UP));
    }

    public BigDecimal getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(BigDecimal sumPrice) {
        this.sumPrice = new BigDecimal(0).setScale(5, RoundingMode.HALF_UP);
        this.sumPrice = this.sumPrice.add(sumPrice.setScale(5, RoundingMode.HALF_UP));
    }
}
