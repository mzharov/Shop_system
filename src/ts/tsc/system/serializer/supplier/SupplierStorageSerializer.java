package ts.tsc.system.serializer.supplier;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ts.tsc.system.entity.supplier.SupplierStorage;

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
        jsonGenerator.writeNumberField("totalSpace",
                storage.getTotalSpace());
        jsonGenerator.writeNumberField("freeSpace",
                storage.getFreeSpace());
        jsonGenerator.writeObjectField("products",
                storage.getProducts());
        jsonGenerator.writeEndObject();
    }
}
