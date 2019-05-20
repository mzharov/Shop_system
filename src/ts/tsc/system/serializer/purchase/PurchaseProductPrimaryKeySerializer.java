package ts.tsc.system.serializer.purchase;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ts.tsc.system.entity.purchase.PurchaseProductPrimaryKey;

import java.io.IOException;

public class PurchaseProductPrimaryKeySerializer extends JsonSerializer<PurchaseProductPrimaryKey> {
    @Override
    public void serialize(PurchaseProductPrimaryKey purchaseProductPrimaryKey, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("deliveryId",
                purchaseProductPrimaryKey.getPurchase().getId());
        jsonGenerator.writeNumberField("productId",
                purchaseProductPrimaryKey.getProduct().getId());
        jsonGenerator.writeEndObject();
    }
}
