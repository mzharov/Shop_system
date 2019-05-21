package ts.tsc.system.service.order.purchase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ts.tsc.system.repository.purchase.PurchaseProductRepository;
import ts.tsc.system.service.base.BaseServiceImplementation;

@Service("purchaseProductService")
public class PurchaseProductService extends BaseServiceImplementation {

    private final PurchaseProductRepository purchaseProductRepository;

    @Autowired
    public PurchaseProductService(PurchaseProductRepository purchaseProductRepository) {
        this.purchaseProductRepository = purchaseProductRepository;
    }

    @Override
    public PurchaseProductRepository getRepository() {
        return this.purchaseProductRepository;
    }
}
