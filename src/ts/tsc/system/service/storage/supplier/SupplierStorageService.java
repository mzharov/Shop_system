package ts.tsc.system.service.storage.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.dto.OwnerProductReportDTO;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.entity.supplier.SupplierStorage;
import ts.tsc.system.entity.supplier.SupplierStorageProduct;
import ts.tsc.system.repository.named.NamedRepository;
import ts.tsc.system.repository.supplier.SupplierRepository;
import ts.tsc.system.repository.supplier.SupplierStorageRepository;
import ts.tsc.system.service.storage.manager.StorageServiceManager;

import java.util.List;

@Service("supplierStorageService")
@Transactional
public class SupplierStorageService
        extends StorageServiceManager<Supplier, SupplierStorageProduct, SupplierStorage, Long> {

    private final SupplierStorageRepository supplierStorageRepository;
    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierStorageService(SupplierStorageRepository supplierStorageRepository,
                                  SupplierRepository supplierRepository) {
        this.supplierStorageRepository = supplierStorageRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public SupplierStorageRepository getRepository() {
        return this.supplierStorageRepository;
    }

    @Override
    public List<SupplierStorage> findStoragesByOwnerId(Long id) {
        return supplierStorageRepository.findByOwnerId(id);
    }

    /**
     * Запрос отчета о количестве товаров на всех складах
     * @return 1) код 200 и необходимый список в теле ответа, если удалось сформировать отчет
     *         2) код 404 с сообщением ELEMENT_NOT_FOUND, если не удалось сформировать отчет
     */
    @Override
    public ResponseEntity<?> getOwnerProductReport() {
        List<OwnerProductReportDTO> list = getRepository().getOwnerProductReportDTO();
        if(list.size() < 1) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND+"", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Override
    public NamedRepository<Supplier, Long> getOwnerService() {
        return supplierRepository;
    }
}
