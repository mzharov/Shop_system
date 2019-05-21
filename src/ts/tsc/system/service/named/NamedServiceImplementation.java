package ts.tsc.system.service.named;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.repository.named.NamedRepository;
import ts.tsc.system.service.base.BaseServiceImplementation;

import java.util.List;

/**
 * Реализация интрефейса для поиска объектов по имени
 * @param <T> Объекты, наследующие от NamedService
 * @param <I> Тип идентификтаора объекта
 */
@Service("namedService")
@Transactional
public abstract class NamedServiceImplementation<T extends NamedEntity, ID>
        extends BaseServiceImplementation<T, ID>
        implements NamedService<T, ID> {

    /**
     * Поиск объекта по имени
     * @param name Строковое значение имени, по которому нужно найти списко объектов
     * @return объект и код 200, если по заданному имени найден результат, иначе - код 404
     */

    @Override
    @Transactional(readOnly = true)
    public List<T> findByName(String name) {
        return getRepository().findByName(name);
    }


    @Override
    public NamedRepository<T, ID> getRepository() {
        return null;
    }
}
