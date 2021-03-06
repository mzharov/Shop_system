package ts.tsc.system.entity.delivery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.serializer.delivery.DeliveryProductPrimaryKeySerializer;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@JsonSerialize(using = DeliveryProductPrimaryKeySerializer.class)
public class DeliveryProductPrimaryKey implements Serializable {
    @ManyToOne
    @JoinColumn(name = "delivery_ID")
    @JsonIgnore
    private Delivery delivery;

    @ManyToOne
    @JoinColumn(name = "product_ID")
    @JsonIgnore
    private Product product;

    public DeliveryProductPrimaryKey() {}

    public DeliveryProductPrimaryKey(Delivery delivery, Product product) {
        this.delivery = delivery;
        this.product = product;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
