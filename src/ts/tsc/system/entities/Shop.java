package ts.tsc.system.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "shops")
public class Shop implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "budget")
    private Long budget;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Long getBudget() {
        return budget;
    }
    public void setBudget(Long budget) {
        this.budget = budget;
    }
}
