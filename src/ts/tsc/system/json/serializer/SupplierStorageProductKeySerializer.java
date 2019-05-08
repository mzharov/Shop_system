package ts.tsc.system.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ts.tsc.system.entities.SupplierStorageProductKey;

import java.io.IOException;

public class SupplierStorageProductKeySerializer extends JsonSerializer<SupplierStorageProductKey> {
    @Override
    public void serialize(SupplierStorageProductKey supplierStorageProductKey,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("storageId",
                supplierStorageProductKey.getStorage().getId());
        jsonGenerator.writeNumberField("productId",
                supplierStorageProductKey.getProduct().getId());
        jsonGenerator.writeEndObject();
    }
}
