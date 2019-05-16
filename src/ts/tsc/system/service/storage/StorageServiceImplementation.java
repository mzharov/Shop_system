package ts.tsc.system.service.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.controllers.status.enums.ErrorStatus;
import ts.tsc.system.entity.parent.BaseStorage;
import ts.tsc.system.service.base.BaseServiceImplementation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Optional;

/**
 * Реализация интерфейса в виде сервиса для управления складами
 * @param <B> Тип данных объекта, которму принадлежит склад
 * @param <T> Тип данных склада
 * @param <ID> Ти данных идентификатора
 */
@Service("storageService")
@Transactional
public class StorageServiceImplementation<B, T extends BaseStorage<B>, ID>
        extends BaseServiceImplementation<T, ID> implements StorageService<B, T, ID>{
    @PersistenceContext
    EntityManager entityManager;

    /**
     * Поиск склада по заданному идентификатору
     * @param id идентификатор
     * @param stringQuery строковый запрос HQL
     * @param repository репозиторий таблицы
     * @return объект, с указанным идентификатором и кодом 200, если найден; иначе код 404
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public ResponseEntity<T> findById(Long id,
                                         String stringQuery,
                                         JpaRepository<T, ID> repository) {
        try {
            Query query = entityManager.createQuery(stringQuery)
                    .setParameter(1, id);
            T storage = (T) query.getSingleResult();
            return ResponseEntity.ok().body(storage);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Добавление склада объекту
     * @param id идентификатор целевого объекта
     * @param storage объект склада
     * @param repositoryBase репозиторий таблицы целевых объектов
     * @param repositoryStorage репозиторий таблицы складов
     * @return объект и код 200, если удалось добавить;
     * 404 - если не йдалось найти целевой объект;
     * 422 с описанием - если в ходе добавления произошла ошибка
     */
    @Override
    public ResponseEntity<?> addStorage(ID id, T storage,
                                        JpaRepository<B, ID> repositoryBase,
                                        JpaRepository<T, ID> repositoryStorage) {
        try {
            Optional<B> optionalSupplier = repositoryBase.findById(id);
            if(optionalSupplier.isPresent()) {
                B base = optionalSupplier.get();
                storage.setOwner(base);
                repositoryStorage.save(storage);
                return ResponseEntity.ok().body(storage);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().body(ErrorStatus.ERROR_WHILE_SAVING);
        }
    }
}
