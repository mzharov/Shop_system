package ts.tsc.system.controller.parent;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.controller.status.OrderStatus;
import ts.tsc.system.entity.parent.BaseEntity;
import ts.tsc.system.entity.parent.BaseStorage;
import ts.tsc.system.service.base.BaseServiceInterface;
import ts.tsc.system.service.order.OrderInterface;

import java.util.Optional;

public abstract class OrderController<B extends BaseEntity, P, T extends BaseStorage<B, P>> {
    protected abstract OrderInterface getService();

    /**
     * Изменения статуса заказа
     * @param id идентификатор заказа
     * @param stringStatus новое состояние
     * @return 1) объект заказа с кодом 200, если успешно,
     *         2) код 400 с описанием UNKNOWN_DELIVER_STATUS, если передано неизвестное состояние,
     *         3) Остальные возможные значения:
     *                      3.1) {@link ts.tsc.system.service.shop.ShopService#deliverOrder(Long)}
     *                      3.2) {@link ts.tsc.system.service.supplier.SupplierService#deliverOrder(Long)}
     *                      3.3) {@link ts.tsc.system.service.shop.ShopService#completeOrder(Long)} (Long)}
     *                      3.4) {@link ts.tsc.system.service.supplier.SupplierService#completeOrder(Long)}
     *                      3.5) {@link ts.tsc.system.service.shop.ShopService#cancelOrder(Long)} (Long)}
     *                      3.6) {@link ts.tsc.system.service.supplier.SupplierService#cancelOrder(Long)}
     */
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @PathVariable String stringStatus) {

        OrderStatus orderStatus;

        try {
             orderStatus = OrderStatus.valueOf(stringStatus);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ErrorStatus.UNKNOWN_DELIVER_STATUS, HttpStatus.BAD_REQUEST);
        }

        if(orderStatus.equals(OrderStatus.DELIVERING)) {
            return getService().deliverOrder(id);
        }
        if(orderStatus.equals(OrderStatus.COMPLETED)) {
            return getService().completeOrder(id);
        }
        if(orderStatus.equals(OrderStatus.CANCELED)) {
            return getService().cancelOrder(id);
        }
        return new ResponseEntity<>(ErrorStatus.WRONG_DELIVERY_STATUS, HttpStatus.BAD_REQUEST);
    }

    /**
     * Получение списка продуктов со склада
     * @param id идентификтаор склада
     * @param repository репозиторий таблицы складов
     * @return 1) склад с указанным идентификатором найден возвращается список товаров с колом 200,
     *         2) если товаров на складе нет возвращается код 404 с сообщением NO_PRODUCTS_IN_STORAGE,
     *         3) если склад с указанным идентификатором не найден код 404 c сообщением ELEMENT_NOT_FOUND
     */
    protected ResponseEntity<?> getStorageProducts(Long id, BaseServiceInterface<T, Long> repository) {
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
