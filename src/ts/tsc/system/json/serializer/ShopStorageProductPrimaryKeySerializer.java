package ts.tsc.system.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ts.tsc.system.entities.keys.ShopStorageProductPrimaryKey;

import java.io.IOException;

public class ShopStorageProductPrimaryKeySerializer extends JsonSerializer<ShopStorageProductPrimaryKey> {
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
