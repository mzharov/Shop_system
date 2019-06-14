package ts.tsc.system.repository.supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ts.tsc.system.dto.OwnerProductReportDTO;
import ts.tsc.system.entity.supplier.SupplierStorage;

import java.util.List;

@Repository
public interface SupplierStorageRepository extends JpaRepository<SupplierStorage, Long> {
    @Query("select entity from SupplierStorage entity where entity.supplier.id = ?1")
    List<SupplierStorage> findByOwnerId(Long id);
    @Query("select new ts.tsc.system.dto.OwnerProductReportDTO(ss.supplier.id, ssp.primaryKey.product.id, sum(ssp.count)) " +
            "from SupplierStorage ss join SupplierStorageProduct ssp on ss.id=ssp.primaryKey.storage.id " +
            "group by ss.supplier.id, ssp.primaryKey.product.id")
    List<OwnerProductReportDTO> getOwnerProductReportDTO();
}
