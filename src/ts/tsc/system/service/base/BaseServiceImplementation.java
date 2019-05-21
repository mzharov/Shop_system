package ts.tsc.system.service.base;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public abstract class BaseServiceImplementation <T, ID> implements BaseService<T, ID> {

    /**
     * Поиск всех элементов в таблице
     * @param repository репозиторий таблицы
     * @return ответ с объектом и кодом 200, если объект не найден - 404
     */

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
    public Optional<T> findById(ID id) {
        return getRepository().findById(id);
    }

    /**
     * Сохранение объекта в таблице
     * @param entity объект
     * @param repository репозиторий таблицы
     * @return ответ с объектом и кодом 200, если объект не найден - код 422 и сообщение с описание ошибки
     */
    @Override
    public T save(T entity) {
        try {
            getRepository().save(entity);
            return entity;
        } catch (Exception e) {
            return null;
        }
    }
}
