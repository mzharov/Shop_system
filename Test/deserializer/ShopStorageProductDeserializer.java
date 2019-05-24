package deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ts.tsc.system.entity.shop.ShopStorageProduct;

import java.io.IOException;
import java.math.BigDecimal;

public class ShopStorageProductDeserializer
        extends JsonDeserializer<ShopStorageProduct> {
    @Override
    public ShopStorageProduct deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        ShopStorageProduct shopStorageProduct = new ShopStorageProduct();
        shopStorageProduct.setCount(node.get("count").asInt());
        shopStorageProduct.setPrice(new BigDecimal(node.get("price").asText()));

        return shopStorageProduct;
    }
}
