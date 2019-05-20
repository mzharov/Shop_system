package ts.tsc.system.serializer.shop;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ts.tsc.system.entity.shop.ShopStorageProductPrimaryKey;

import java.io.IOException;

public class ShopStorageProductPrimaryKeySerializer
        extends JsonSerializer<ShopStorageProductPrimaryKey> {
    @Override
    public void serialize(ShopStorageProductPrimaryKey productPrimaryKey, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("storageId",
                productPrimaryKey.getStorage().getId());
        jsonGenerator.writeNumberField("productId",
                productPrimaryKey.getProduct().getId());
        jsonGenerator.writeEndObject();
    }
}
