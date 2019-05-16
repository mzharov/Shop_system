package ts.tsc.system.entity.parent;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class NamedEntity<ID> extends BaseEntity<ID> implements Serializable {
    @Column(name = "name")
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
