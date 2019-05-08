package ts.tsc.system.services.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entities.Supplier;
import ts.tsc.system.repositories.SupplierRepository;
import ts.tsc.system.services.interfaces.SupplierService;

import java.util.ArrayList;
import java.util.List;

@Service("supplierService")
@Transactional
public class SupplierRepositoryImplementation implements SupplierService<Supplier> {

    private final
    SupplierRepository shopRepository;

    @Autowired
    public SupplierRepositoryImplementation(SupplierRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> findAll() {
        Iterable<Supplier> iterable = shopRepository.findAll();
        List<Supplier> shops = new ArrayList<>();
        iterable.forEach(shops::add);
        return shops;
    }

    @Override
    public Supplier findById(Long id) {
        return shopRepository.findById(id).get();
    }

    @Override
    public Supplier save(Supplier shop) {
        return shopRepository.save(shop);
    }

    @Override
    public void delete(Supplier shop) {
        shopRepository.delete(shop);
    }
}
