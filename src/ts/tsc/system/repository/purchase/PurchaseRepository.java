package ts.tsc.system.repository.purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ts.tsc.system.dto.PurchaseReportDTO;
import ts.tsc.system.entity.purchase.Purchase;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    @Query("select new ts.tsc.system.dto.PurchaseReportDTO(p.orderStatus, p.shop.id, sum(pp.sumPrice)) " +
            "from Purchase p " +
            "join PurchaseProduct pp on p.id=pp.primaryKey.purchase.id where p.orderStatus = 'COMPLETED' " +
            "group by p.orderStatus, p.shop.id")
    List<PurchaseReportDTO> getPurchaseReport();
}
