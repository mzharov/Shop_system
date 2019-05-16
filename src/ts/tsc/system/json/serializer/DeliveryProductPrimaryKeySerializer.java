package ts.tsc.system.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ts.tsc.system.entities.keys.DeliveryProductPrimaryKey;

import java.io.IOException;

public class DeliveryProductPrimaryKeySerializer extends JsonSerializer<DeliveryProductPrimaryKey> {
    @Override
    public void serialize(DeliveryProductPrimaryKey deliveryProductPrimaryKey, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("deliveryId",
                deliveryProductPrimaryKey.getDelivery().getId());
        jsonGenerator.writeNumberField("productId",
                deliveryProductPrimaryKey.getProduct().getId());
        jsonGenerator.writeEndObject();
    }
}
