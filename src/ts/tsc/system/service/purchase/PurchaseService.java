package ts.tsc.system.service.purchase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entity.purchase.Purchase;
import ts.tsc.system.repository.purchase.PurchaseRepository;
import ts.tsc.system.service.base.BaseServiceImplementation;

@Service("purchaseService")
@Transactional
public class PurchaseService extends BaseServiceImplementation<Purchase, Long> {

    @Autowired
    private final PurchaseRepository purchaseRepository;

    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public PurchaseRepository getRepository() {
        return this.purchaseRepository;
    }
}
