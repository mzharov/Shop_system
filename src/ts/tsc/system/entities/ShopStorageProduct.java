package ts.tsc.system.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ts.tsc.system.json.serializer.ShopStorageProductPrimaryKeySerializer;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ShopStoragesProduct")
public class ShopStorageProduct implements Serializable {

    @EmbeddedId
    private ShopStorageProductPrimaryKey primaryKey;

    @Column(name = "count")
    private int count;

    @Column(name="price")
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
        this.price = price;
    }
}
