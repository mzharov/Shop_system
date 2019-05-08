package ts.tsc.system.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ts.tsc.system.entities.SupplierStorage;

import java.io.IOException;

public class SupplierStorageSerializer extends JsonSerializer<SupplierStorage> {
    @Override
    public void serialize(SupplierStorage storage,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id",
                storage.getId());
        jsonGenerator.writeNumberField("supplierId",
                storage.getSupplier().getId());
        jsonGenerator.writeObjectField("products",
                storage.getProducts());
        jsonGenerator.writeNumberField("totalSpace",
                storage.getTotalSpace());
        jsonGenerator.writeNumberField("freeSpace",
                storage.getFreeSpace());
        jsonGenerator.writeEndObject();
    }
}
