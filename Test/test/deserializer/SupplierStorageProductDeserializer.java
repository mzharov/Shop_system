package test.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ts.tsc.system.entity.supplier.SupplierStorageProduct;

import java.io.IOException;
import java.math.BigDecimal;

public class SupplierStorageProductDeserializer extends JsonDeserializer<SupplierStorageProduct> {
    @Override
    public SupplierStorageProduct deserialize(JsonParser jsonParser,
                                              DeserializationContext deserializationContext)
            throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        SupplierStorageProduct supplierStorageProduct = new SupplierStorageProduct();
        supplierStorageProduct.setCount(node.get("count").asInt());
        supplierStorageProduct.setPrice(new BigDecimal(node.get("price").asText()));

        return supplierStorageProduct;
    }
}
