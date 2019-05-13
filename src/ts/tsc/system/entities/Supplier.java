package ts.tsc.system.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "suppliers")
public class Supplier extends NamedEntity<Long> implements Serializable {
    @OneToMany(mappedBy = "supplier",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<SupplierStorage> storages = new HashSet<>();

    public Set<SupplierStorage> getStorages() {
        return storages;
    }
    public void setStorages(Set<SupplierStorage> storages) {
        this.storages = storages;
    }
}
