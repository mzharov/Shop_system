package ts.tsc.system.entity.shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.entity.purchase.Purchase;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "shop")
public class Shop extends NamedEntity<Long> implements Serializable {


    @Column(name = "budget", precision = 20, scale = 5)
    private BigDecimal budget;

    @OneToMany(mappedBy = "shop",
            fetch = FetchType.EAGER)
    private Set<ShopStorage> storages = new HashSet<>();

    @OneToMany(mappedBy = "shop",
            fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Purchase> purchases = new HashSet<>();

    public BigDecimal getBudget() {
        return budget;
    }
    public void setBudget(BigDecimal budget) {
        this.budget = new BigDecimal(0).setScale(5, RoundingMode.HALF_UP);
        this.budget = this.budget.add(budget.setScale(5, RoundingMode.HALF_UP));
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
