package ts.tsc.system.entity.delivery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ts.tsc.system.controllers.status.enums.Status;
import ts.tsc.system.entity.shop.ShopStorage;
import ts.tsc.system.entity.supplier.SupplierStorage;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "delivery")
public class Delivery implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "status")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "shopStorageID")
    @JsonIgnore
    private ShopStorage shopStorage;

    @ManyToOne
    @JoinColumn(name = "supplierStorageID")
    @JsonIgnore
    private SupplierStorage supplierStorage;

    @OneToMany(mappedBy = "primaryKey.delivery",
            fetch = FetchType.EAGER)
    private Set<DeliveryProduct> deliveryProducts = new HashSet<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ShopStorage getShopStorage() {
        return shopStorage;
    }

    public void setShopStorage(ShopStorage shopStorage) {
        this.shopStorage = shopStorage;
    }

    public SupplierStorage getSupplierStorage() {
        return supplierStorage;
    }

    public void setSupplierStorage(SupplierStorage supplierStorage) {
        this.supplierStorage = supplierStorage;
    }

    public Set<DeliveryProduct> getDeliveryProducts() {
        return deliveryProducts;
    }

    public void setDeliveryProducts(Set<DeliveryProduct> deliveryProducts) {
        this.deliveryProducts = deliveryProducts;
    }
}
