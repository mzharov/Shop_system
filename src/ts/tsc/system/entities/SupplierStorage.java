package ts.tsc.system.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ts.tsc.system.json.serializer.SupplierStorageSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "supplier_storage")
@JsonSerialize(using = SupplierStorageSerializer.class)
public class SupplierStorage implements Serializable, BaseStorage<Supplier> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    @JsonIgnore
    private Supplier supplier;

    @OneToMany(mappedBy = "primaryKey.storage",
            fetch = FetchType.EAGER)
    private Set<SupplierStorageProduct> products = new HashSet<>();

    @Column(name = "total_space")
    private int totalSpace;

    @Column(name = "free_space")
    private int freeSpace;

    public Supplier getSupplier() {
        return supplier;
    }

    @Override
    public void setOwner(Supplier supplier) {
        setSupplier(supplier);
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
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

    public Set<SupplierStorageProduct> getProducts() {
        return products;
    }

    public void setProducts(Set<SupplierStorageProduct> products) {
        this.products = products;
    }
}
