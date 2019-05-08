package ts.tsc.system.entities;

import javax.persistence.*;

@Entity
@Table(name = "suppliers_storages")
public class SuppliersStorages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "total_space")
    private int totalSpace;

    @Column(name = "free_space")
    private int freeSpace;
    
}
