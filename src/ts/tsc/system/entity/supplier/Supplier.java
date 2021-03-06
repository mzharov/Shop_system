package ts.tsc.system.entity.supplier;

import ts.tsc.system.entity.parent.NamedEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "suppliers")
public class Supplier extends NamedEntity<Long> implements Serializable {
    @OneToMany(mappedBy = "supplier",
            fetch = FetchType.EAGER)
    private Set<SupplierStorage> storages = new HashSet<>();

    public Set<SupplierStorage> getStorages() {
        return storages;
    }
    public void setStorages(Set<SupplierStorage> storages) {
        this.storages = storages;
    }
}
