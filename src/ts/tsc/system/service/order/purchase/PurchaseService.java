package ts.tsc.system.service.order.purchase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entity.purchase.Purchase;
import ts.tsc.system.repository.purchase.PurchaseRepository;
import ts.tsc.system.service.base.BaseService;

@Service("purchaseService")
@Transactional
public class PurchaseService extends BaseService<Purchase, Long> {

    private final PurchaseRepository purchaseRepository;

    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public PurchaseRepository getRepository() {
        return this.purchaseRepository;
    }
}
