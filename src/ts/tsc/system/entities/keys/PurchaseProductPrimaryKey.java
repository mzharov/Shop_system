package ts.tsc.system.entities.keys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ts.tsc.system.entities.Product;
import ts.tsc.system.entities.Purchase;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
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
