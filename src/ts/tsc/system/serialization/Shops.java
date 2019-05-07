package ts.tsc.system.serialization;

import ts.tsc.system.entities.Shop;

import java.io.Serializable;
import java.util.List;

public class Shops implements Serializable {
    private List<Shop> shops;

    public Shops() {

    }

    public Shops(List<Shop> shops) {
        this.shops = shops;
    }

    public List<Shop> getShops() {
        return shops;
    }
    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }
}
