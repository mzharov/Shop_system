package ts.tsc.system.entity.shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ts.tsc.system.entity.delivery.Delivery;
import ts.tsc.system.entity.parent.BaseStorage;
import ts.tsc.system.serializer.shop.ShopStorageSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "shop_storage")
@JsonSerialize(using = ShopStorageSerializer.class)
public class ShopStorage implements Serializable, BaseStorage<Shop, ShopStorageProduct> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type")
    private int type;

    @Column(name = "total_space")
    private int totalSpace;

    @Column(name = "free_space")
    private int freeSpace;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @OneToMany(mappedBy = "primaryKey.storage",
            fetch = FetchType.EAGER)
    private Set<ShopStorageProduct> products = new HashSet<>();

    @OneToMany(mappedBy = "shopStorage",
            fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Delivery> deliveries = new HashSet<>();

    @Override
    public void setOwner(Shop shop) {
        setShop(shop);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getTotalSpace() {
        return totalSpace;
    }
    public void setTotalSpace(int totalSpace) {
        this.totalSpace = totalSpace;
    }
    public int getFreeSpace() {
        return freeSpace;
    }
    public void setFreeSpace(int freeSpace) {
        this.freeSpace = freeSpace;
    }
    public Shop getShop() {
        return shop;
    }
    public void setShop(Shop shop) {
        this.shop = shop;
    }
    public Set<ShopStorageProduct> getProducts() {
        return products;
    }
    public void setProducts(Set<ShopStorageProduct> products) {
        this.products = products;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public Set<Delivery> getDeliveries() {
        return deliveries;
    }
    public void setDeliveries(Set<Delivery> deliveries) {
        this.deliveries = deliveries;
    }
}
