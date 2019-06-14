package ts.tsc.system.entity.delivery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ts.tsc.system.controller.status.OrderStatus;
import ts.tsc.system.entity.parent.OrderEntity;
import ts.tsc.system.entity.shop.ShopStorage;
import ts.tsc.system.entity.supplier.SupplierStorage;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "delivery")
public class Delivery implements Serializable, OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "orderStatus")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name = "shop_storage_ID")
    @JsonIgnore
    private ShopStorage shopStorage;

    @ManyToOne
    @JoinColumn(name = "supplier_storage_ID")
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

    @Override
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
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
