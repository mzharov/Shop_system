package ts.tsc.system.entity.purchase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.serializer.purchase.PurchaseProductPrimaryKeySerializer;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@JsonSerialize(using = PurchaseProductPrimaryKeySerializer.class)
public class PurchaseProductPrimaryKey implements Serializable {
    @ManyToOne
    @JoinColumn(name = "purchaseID")
    @JsonIgnore
    private Purchase purchase;

    @ManyToOne
    @JoinColumn(name = "productID")
    @JsonIgnore
    private Product product;

    public PurchaseProductPrimaryKey() {}

    public PurchaseProductPrimaryKey(Purchase purchase, Product product) {
        this.purchase = purchase;
        this.product = product;
    }

    public Purchase getPurchase() {
        return purchase;
    }
    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
}
