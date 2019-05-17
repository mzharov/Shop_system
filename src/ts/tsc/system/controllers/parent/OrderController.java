package ts.tsc.system.controllers.parent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import ts.tsc.system.controllers.status.enums.ErrorStatus;
import ts.tsc.system.controllers.status.enums.Status;
import ts.tsc.system.entity.parent.BaseStorage;
import ts.tsc.system.entity.parent.OrderEntity;

import java.util.Optional;

public abstract class OrderController<B, P, T extends BaseStorage<B, P>> {
    protected abstract ResponseEntity<?> deliverOrder(Long id);
    protected abstract ResponseEntity<?> completeOrder(Long id);
    protected abstract ResponseEntity<?> cancelOrder(Long id);

    /**
     * Изменения статуса заказа
     * @param id идентификатор заказа
     * @param stringStatus новое состояние
     * @return объект заказа с кодом 200, если успешно,
     *      * код 400 с описанием UNKNOWN_DELIVER_STATUS, если передано неизвестное состояние,
     *      * либо результаты {@link #deliverOrder(Long)},
     *      * {@link #cancelOrder(Long)},
     *      * {@link #completeOrder(Long)}
     */
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @PathVariable String stringStatus) {

        Status status;

        try {
             status = Status.valueOf(stringStatus);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ErrorStatus.UNKNOWN_DELIVER_STATUS, HttpStatus.BAD_REQUEST);
        }

        if(status.equals(Status.DELIVERING)) {
            return deliverOrder(id);
        }
        if(status.equals(Status.COMPLETED)) {
            return completeOrder(id);
        }
        return cancelOrder(id);
    }
    protected boolean isNotCancelable(OrderEntity orderEntity) {
        return !(orderEntity.getStatus().equals(Status.RECEIVED)
                || orderEntity.getStatus().equals(Status.DELIVERING));
    }

    /**
     * Получение списка продуктов со склада
     * @param id идентификтаор склада
     * @param repository репозиторий таблицы складов
     * @return если склад с указанным идентификатором найден возвращается список товаров с колом 200,
     *      * иначе если товаров на складе нет возвращается код 404 с сообщением NO_PRODUCTS_IN_STORAGE,
     *      * или если склад с указанным идентификатором не найден код 404 c сообщением ELEMENT_NOT_FOUND
     */
    protected ResponseEntity<?> getStorageProducts(Long id, JpaRepository<T, Long> repository) {
        Optional<T> storageOptional = repository.findById(id);
        if(!storageOptional.isPresent()) {
            return new ResponseEntity<>(ErrorStatus.ELEMENT_NOT_FOUND,
                    HttpStatus.NOT_FOUND);
        }
        T storage = storageOptional.get();
        if(storage.getProducts().size() > 0) {
            return ResponseEntity.ok().body(storage.getProducts());
        } else {
            return new ResponseEntity<>(ErrorStatus.NO_PRODUCTS_IN_STORAGE,
                    HttpStatus.NOT_FOUND);
        }
    }

}
