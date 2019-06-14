package ts.tsc.system.repository.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ts.tsc.system.dto.DeliveryReportDTO;
import ts.tsc.system.entity.delivery.Delivery;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    @Query("select new ts.tsc.system.dto.DeliveryReportDTO(p.orderStatus, p.supplierStorage.id, sum(pp.sumPrice)) " +
            "from Delivery p " +
            "join DeliveryProduct pp on p.id=pp.primaryKey.delivery.id where p.orderStatus = 'COMPLETED' " +
            "group by p.orderStatus, p.supplierStorage.id")
    List<DeliveryReportDTO> getPurchaseReport();
}
