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

@RestController
public abstract class BaseController
        <ENTITY extends NamedEntity<ID>, SERVICE extends NamedServiceInterface<ENTITY, ID>, ID> {
    /**
     * Поиск всех элементов
     * @return {@link BaseService#findAll()}
     */
    @GetMapping(value = "/list")
    public ResponseEntity<?> findAll() {
        return getResponseBuilder().getAll(getService().findAll());
    }

    /**
     * Поиск сущности по идентификатору
     * @param id идентификатор
     * @return {@link BaseService#findById(Object)}
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable ID id) {
        Optional<ENTITY> deliveryOptional = getService().findById(id);
        return deliveryOptional.<ResponseEntity<?>>map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    /**
     * Обновление  сущности в таблице
     * @param id идентификатора
     * @param entity объект
     * @return 1) объект с кодом 200, если удалось обновить;
     *         2) код 404 - если не удалось найти объект с указанным идентификатором
     *         3) код 400 с сообщением ID_CAN_NOT_BE_SET_IN_JSON, если в теле json задан идентификатор
     */
    @PutMapping(value = "/{id}")
    @IDValidation
    public ResponseEntity<?> update(@PathVariable ID id, @RequestBody ENTITY entity) {
        Optional<ENTITY> productOptional = getService().findById(id);
        if(productOptional.isPresent()) {
            return getResponseBuilder().save(getService().update(id, entity));
        } else {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND + ":product", HttpStatus.NOT_FOUND);
        }
    }

    abstract protected BaseResponseBuilder<ENTITY> getResponseBuilder();
    abstract protected SERVICE getService();
}
