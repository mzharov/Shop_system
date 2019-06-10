package ts.tsc.system.controller.parent;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ts.tsc.system.controller.aspect.IDValidation;
import ts.tsc.system.controller.response.BaseResponseBuilder;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.entity.parent.NamedEntity;
import ts.tsc.system.service.base.BaseService;
import ts.tsc.system.service.named.NamedServiceInterface;
import ts.tsc.system.service.product.ProductService;

import java.util.Optional;

public abstract class ExtendedBaseController
        <ENTITY extends NamedEntity<ID>, SERVICE extends NamedServiceInterface<ENTITY, ID>, ID>
        extends BaseController<ENTITY, SERVICE, ID> {
    /**
     * Добавление нового товара на склад
     * @param entity объект, представляющий товар
     * @return 1) код 400 с сообщением ID_CAN_NOT_BE_SET_IN_JSON, если в теле json задан идентификатор
     *         2) {@link ProductService#save(Object)}
     */
    @PostMapping(value = "/")
    @IDValidation
    public ResponseEntity<?> create(@RequestBody ENTITY entity) {
        return getResponseBuilder().save(getService().save(entity));
    }
}
