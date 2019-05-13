package ts.tsc.system.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ts.tsc.system.json.serializer.SupplierStorageProductPrimaryKeySerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "SupplierStorageProduct")
public class SupplierStorageProduct implements Serializable {

    @EmbeddedId
    @JsonSerialize(using = SupplierStorageProductPrimaryKeySerializer.class)
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