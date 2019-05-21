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
public abstract class BaseService<T, ID> implements BaseServiceInterface<T, ID> {

    /**
     * Поиск всех элементов в таблице
     * @return 1) список объектов если результат найден
     *         2) пустой список, если ничего не найдено
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
     * @return объект типа Optional
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) {
        return getRepository().findById(id);
    }

    /**
     * Сохранение объекта в таблице
     * @param entity объект
     * @return 1) объект, если удалось сохранить в БД
     *         2) null, если в ходе сохранения произошла ошибка
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
