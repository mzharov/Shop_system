package ts.tsc.system.entities;

import javax.persistence.*;

@MappedSuperclass
public abstract class BaseEntity<ID> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private ID id;

    public ID getId() {
        return id;
    }
    public void setId(ID id) {
        this.id = id;
    }
}
