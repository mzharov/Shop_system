package ts.tsc.system.service.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.entity.parent.BaseStorage;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.service.base.BaseServiceImplementation;
import ts.tsc.system.service.named.NamedService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса в виде сервиса для управления складами
 * @param <B> Тип данных объекта, которму принадлежит склад
 * @param <T> Тип данных склада
 * @param <ID> Ти данных идентификатора
 */
@Service("storageService")
@Transactional
public class StorageServiceImplementation<B extends NamedEntity<ID>, P, T extends BaseStorage<B, P>, ID>
        extends BaseServiceImplementation<T, ID> implements StorageService<B, T, ID>{
    @PersistenceContext
    EntityManager entityManager;

    /**
     * Поиск складов по заданному запросу
     * @param id входной параметр
     * @param stringQuery строковый запрос HQL
     * @return объект, с указанным идентификатором и кодом 200, если найден; иначе код 404
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> findById(Long id, String stringQuery) {
        try {
            Query query = entityManager.createQuery(stringQuery)
                    .setParameter(1, id);
            List<T> storage = query.getResultList();
            if(storage.size() > 0) {
                return ResponseEntity.ok().body(storage);
            } else throw new Exception();
        } catch (Exception e) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Добавление склада объекту
     * @param id идентификатор целевого объекта
     * @param storage объект склада
     * @param namedService репозиторий таблицы целевых объектов
     * @return 1) код 200 и объект, если удалось добавить;
     *         2) код 404 - если не йдалось найти целевой объект;
     *         3) код 422 с описанием - если в ходе добавления произошла ошибка
     */
    @Override
    public ResponseEntity<?> addStorage(ID id, T storage, NamedService<B, ID> namedService) {
        try {
            Optional<B> optionalSupplier = namedService.findById(id);
            if(optionalSupplier.isPresent()) {
                B base = optionalSupplier.get();
                storage.setOwner(base);
                getRepository().save(storage);
                return ResponseEntity.ok().body(storage);
            } else {
                return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().body(ErrorStatus.ERROR_WHILE_SAVING);
        }
    }
}
