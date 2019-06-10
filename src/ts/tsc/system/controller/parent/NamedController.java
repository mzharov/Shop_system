package ts.tsc.system.controller.parent;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.service.named.NamedService;
import ts.tsc.system.service.named.NamedServiceInterface;

@RestController
public abstract class NamedController<ENTITY extends NamedEntity<ID>,
        SERVICE extends NamedServiceInterface<ENTITY, ID>, ID>
        extends ExtendedBaseController<ENTITY, SERVICE, ID> {

    /**
     * Поиск сущности по названию
     * @param name название сущности
     * @return {@link NamedService#findByName(String)}
     */
    @GetMapping(value = "/name/{name}")
    public ResponseEntity<?> findByName(@PathVariable String name) {
        return getResponseBuilder().getAll(getService().findByName(name));
    }
}
