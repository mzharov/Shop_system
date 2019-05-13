package ts.tsc.system.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ts.tsc.system.entities.keys.PurchaseProductPrimaryKey;
import ts.tsc.system.json.serializer.ProductSerializer;
import ts.tsc.system.json.serializer.SupplierStorageProductPrimaryKeySerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product")
@JsonSerialize(using = ProductSerializer.class)
public class Product extends NamedEntity<Long> implements Serializable {


    @Column(name = "category")
    private String category;

    @OneToMany(mappedBy = "primaryKey.product",
            fetch = FetchType.EAGER)
    private Set<SupplierStorageProduct> products = new HashSet<>();

    @OneToMany(mappedBy = "primaryKey.product",
            fetch = FetchType.EAGER)
    private Set<DeliveryProduct> deliveryProducts = new HashSet<>();

    @OneToMany(mappedBy = "primaryKey.purchase",
            fetch = FetchType.EAGER)
    private Set<PurchaseProduct> purchaseProducts = new HashSet<>();

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public Set<SupplierStorageProduct> getProducts() {
        return products;
    }

    public void setProducts(Set<SupplierStorageProduct> products) {
        this.products = products;
    }

    public Set<DeliveryProduct> getDeliveryProducts() {
        return deliveryProducts;
    }

    public void setDeliveryProducts(Set<DeliveryProduct> deliveryProducts) {
        this.deliveryProducts = deliveryProducts;
    }

    public Set<PurchaseProduct> getPurchaseProducts() {
        return purchaseProducts;
    }

    public void setPurchaseProducts(Set<PurchaseProduct> purchaseProducts) {
        this.purchaseProducts = purchaseProducts;
    }
}
