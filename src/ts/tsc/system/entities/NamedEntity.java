package ts.tsc.system.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class NamedEntity<ID> extends BaseEntity<ID>{
    @Column(name = "name")
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
