package ts.tsc.system.service.named;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.service.base.BaseService;

import java.util.List;

/**
 * Реализация интрефейса для поиска объектов по имени
 * @param <T> Объекты, наследующие от NamedServiceInterface
 * @param <ID> Тип идентификтаора объекта
 */
@Service("namedService")
@Transactional
public abstract class NamedService<T extends NamedEntity, ID>
        extends BaseService<T, ID>
        implements NamedServiceInterface<T, ID> {

    /**
     * Поиск объекта по имени
     * @param name Строковое значение имени, по которому нужно найти списко объектов
     * @return список подходящих объектов (может быть пустой)
     */
    @Override
    @Transactional(readOnly = true)
    public List<T> findByName(String name) {
        return getRepository().findByName(name);
    }
}
