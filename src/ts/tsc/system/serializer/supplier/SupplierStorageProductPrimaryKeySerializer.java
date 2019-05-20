package ts.tsc.system.serializer.supplier;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ts.tsc.system.entity.supplier.SupplierStorageProductPrimaryKey;

import java.io.IOException;

public class SupplierStorageProductPrimaryKeySerializer extends JsonSerializer<SupplierStorageProductPrimaryKey> {
    @Override
    public void serialize(SupplierStorageProductPrimaryKey supplierStorageProductKey,
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
