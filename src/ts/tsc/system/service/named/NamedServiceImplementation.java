package ts.tsc.system.service.named;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.repository.named.NamedRepository;
import ts.tsc.system.service.base.BaseServiceImplementation;

/**
 * Реализация интрефейса для поиска объектов по имени
 * @param <T> Объекты, наследующие от NamedService
 * @param <I> Тип идентификтаора объекта
 */
@Service("namedService")
@Transactional
public class NamedServiceImplementation<T extends NamedEntity,I>
        extends BaseServiceImplementation<T,I>
        implements NamedService<T,I> {

    /**
     * Поиск объекта по имени
     * @param name Строковое значение имени, по которому нужно найти списко объектов
     * @param repository репозиторий таблицы
     * @return объект и код 200, если по заданному имени найден результат, иначе - код 404
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> findByName(String name, NamedRepository<T, I> repository) {
        return repository.findByName(name).map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }
}
