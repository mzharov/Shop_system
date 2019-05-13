package ts.tsc.system.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ts.tsc.system.controllers.status.Status;
import ts.tsc.system.entities.keys.PurchaseProductPrimaryKey;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "purchase")
public class Purchase implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shopID")
    @JsonIgnore
    private Shop shop;

    @OneToMany(mappedBy = "primaryKey.purchase",
            fetch = FetchType.EAGER)
    private Set<PurchaseProduct> purchaseProducts = new HashSet<>();

    @Column(name = "status")
    private Status status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<PurchaseProduct> getPurchaseProducts() {
        return purchaseProducts;
    }

    public void setPurchaseProducts(Set<PurchaseProduct> purchaseProducts) {
        this.purchaseProducts = purchaseProducts;
    }
}
