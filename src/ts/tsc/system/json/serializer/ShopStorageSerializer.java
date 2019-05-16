package ts.tsc.system.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ts.tsc.system.entities.ShopStorage;

import java.io.IOException;

public class ShopStorageSerializer extends JsonSerializer<ShopStorage> {
    @Override
    public void serialize(ShopStorage storage,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id",
                storage.getId());
        jsonGenerator.writeNumberField("shopId",
                storage.getShop().getId());
        jsonGenerator.writeNumberField("type",
                storage.getType());
        jsonGenerator.writeNumberField("totalSpace",
                storage.getTotalSpace());
        jsonGenerator.writeNumberField("freeSpace",
                storage.getFreeSpace());
        jsonGenerator.writeObjectField("products",
                storage.getProducts());
        jsonGenerator.writeEndObject();
    }
}
