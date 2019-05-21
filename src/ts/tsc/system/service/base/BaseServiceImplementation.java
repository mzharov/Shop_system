package ts.tsc.system.service.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.controller.status.ErrorStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса основных функций в виде сервиса
 * @param <T> Тип сущности
 * @param <ID> Тип идентификатора сущности
 */
@Service("baseService")
@Transactional
public class BaseServiceImplementation <T, ID> implements BaseService<T, ID> {

    /**
     * Поиск всех элементов в таблице
     * @param repository репозиторий таблицы
     * @return ответ с объектом и кодом 200, если объект не найден - 404
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> findAll(JpaRepository<T, ID> repository) {
        Iterable<T> iterable = repository.findAll();
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        if (list.size() > 0) {
            return ResponseEntity.ok().body(list);
        } else {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        Iterable<T> iterable = getRepository().findAll();
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }


    /**
     * Поиск по идентификатору
     * @param id идентификтатор объекта
     * @param repository репозиторий таблицы
     * @return ответ с объектом и кодом 200, если объект не найден - 404
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> findById(ID id, JpaRepository<T, ID> repository) {
        Optional<T> optionalT = repository.findById(id);
        return optionalT.<ResponseEntity<?>>map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) {
        return getRepository().findById(id);
    }

    /*
     * Сохранение объекта в таблице
     * @param entity объект
     * @param repository репозиторий таблицы
     * @return ответ с объектом и кодом 200, если объект не найден - код 422 и сообщение с описание ошибки
     */
    @Override
    public ResponseEntity<?> save(T entity, JpaRepository<T, ID> repository) {
        try {
            repository.save(entity);
            return ResponseEntity.ok().body(entity);
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().body(ErrorStatus.ERROR_WHILE_SAVING);
        }
    }

    @Override
    public T save(T entity) {
        try {
            getRepository().save(entity);
            return entity;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public T update(ID id, T entity) {
        return null;
    }

    @Override
    public JpaRepository<T, ID> getRepository() {
        return null;
    }
}
