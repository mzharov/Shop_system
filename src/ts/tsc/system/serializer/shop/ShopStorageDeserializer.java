package ts.tsc.system.serializer.shop;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ts.tsc.system.entity.shop.ShopStorage;
import ts.tsc.system.entity.shop.ShopStorageProduct;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShopStorageDeserializer extends JsonDeserializer<ShopStorage> {
    @Override
    public ShopStorage deserialize(JsonParser jsonParser,
                                   DeserializationContext deserializationContext)
            throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        ShopStorage shopStorage = new ShopStorage();
        shopStorage.setId(node.get("id").asLong());
        shopStorage.setType(node.get(("type")).asInt());
        shopStorage.setFreeSpace(node.get("freeSpace").asInt());
        shopStorage.setTotalSpace(node.get("totalSpace").asInt());

        List<String> countStringList = node.findValuesAsText("count");
        List<String> priceStringList = node.findValuesAsText("price");

        Set<ShopStorageProduct> shopStorageProductSet = new HashSet<>();
        for(int iterator = 0; iterator < countStringList.size(); iterator++) {
            ShopStorageProduct shopStorageProduct = new ShopStorageProduct();
            shopStorageProduct.setCount(Integer.valueOf(countStringList.get(iterator)));
            shopStorageProduct.setPrice(new BigDecimal(priceStringList.get(iterator)));
            shopStorageProductSet.add(shopStorageProduct);
        }

        shopStorage.setProducts(shopStorageProductSet);
        return shopStorage;
    }
}
