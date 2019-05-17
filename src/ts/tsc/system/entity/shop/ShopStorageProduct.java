package ts.tsc.system.entity.shop;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "shop_storage_product")
public class ShopStorageProduct implements Serializable {

    @EmbeddedId
    private ShopStorageProductPrimaryKey primaryKey;

    @Column(name = "count")
    private int count;

    @Column(name="price", precision = 20, scale = 5)
    private BigDecimal price;

    public ShopStorageProductPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(ShopStorageProductPrimaryKey primaryKey) {
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
}
