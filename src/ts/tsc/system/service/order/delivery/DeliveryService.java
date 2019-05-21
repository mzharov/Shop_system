package ts.tsc.system.service.order.delivery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entity.delivery.Delivery;
import ts.tsc.system.repository.delivery.DeliveryRepository;
import ts.tsc.system.service.base.BaseServiceImplementation;

@Service("deliveryService")
@Transactional
public class DeliveryService extends BaseServiceImplementation<Delivery, Long> {

    private final DeliveryRepository deliveryRepository;

    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override
    public DeliveryRepository getRepository() {
        return this.deliveryRepository;
    }
}
