package ts.tsc.system.service.storage.manager;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.entity.parent.BaseStorage;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.repository.named.NamedRepository;
import ts.tsc.system.service.base.BaseService;

import java.util.Optional;

/**
 * Реализация интерфейса в виде сервиса для управления складами
 * @param <B> Тип данных объекта, которму принадлежит склад
 * @param <T> Тип данных склада
 * @param <ID> Тип данных идентификатора
 */
@Service("storageService")
@Transactional
public abstract class StorageServiceManager<B extends NamedEntity<ID>, P, T extends BaseStorage<B, P>, ID>
        extends BaseService<T, ID> implements StorageServiceInterface<T, ID> {

    /**
     * Добавление склада объекту
     * @param id идентификатор целевого объекта
     * @param storage объект склада
     * @return 1) код 200 и объект, если удалось добавить склад;
     *         2) код 404 - если не удалось найти целевой объект;
     *         3) код 500 - если в ходе добавления произошла ошибка
     */
    @Override
    public ResponseEntity<?> addStorage(ID id, T storage) {
        try {
            Optional<B> optionalSupplier = getOwnerService().findById(id);
            if(optionalSupplier.isPresent()) {
                B base = optionalSupplier.get();
                storage.setOwner(base);
                getRepository().save(storage);
                return new ResponseEntity<>(storage, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(ErrorStatus.ERROR_WHILE_SAVING, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected abstract NamedRepository<B, ID> getOwnerService();
}
