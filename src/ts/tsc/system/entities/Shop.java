package ts.tsc.system.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "shop")
public class Shop extends NamedEntity<Long> implements Serializable {

    @OneToMany(mappedBy = "shop",
            fetch = FetchType.EAGER)
    private Set<ShopStorage> storages = new HashSet<>();

    @OneToMany(mappedBy = "shop",
            fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Purchase> purchases = new HashSet<>();

    @Column(name = "budget")
    private BigDecimal budget;

    public BigDecimal getBudget() {
        return budget;
    }
    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public Set<ShopStorage> getStorages() {
        return storages;
    }

    public void setStorages(Set<ShopStorage> storages) {
        this.storages = storages;
    }

    public Set<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(Set<Purchase> purchases) {
        this.purchases = purchases;
    }
}
