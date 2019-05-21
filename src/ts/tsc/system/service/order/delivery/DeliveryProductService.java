package ts.tsc.system.service.order.delivery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ts.tsc.system.repository.delivery.DeliveryProductRepository;
import ts.tsc.system.service.base.BaseService;

@Service("deliveryProductService")
public class DeliveryProductService extends BaseService {

    private final DeliveryProductRepository deliveryProductRepository;

    @Autowired
    public DeliveryProductService(DeliveryProductRepository deliveryProductRepository) {
        this.deliveryProductRepository = deliveryProductRepository;
    }

    @Override
    public DeliveryProductRepository getRepository() {
        return this.deliveryProductRepository;
    }
}
