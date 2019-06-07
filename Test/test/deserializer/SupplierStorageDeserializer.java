package test.deserializer;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ts.tsc.system.entity.supplier.SupplierStorage;
import ts.tsc.system.entity.supplier.SupplierStorageProduct;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SupplierStorageDeserializer extends JsonDeserializer<SupplierStorage> {

    @Override
    public SupplierStorage deserialize(JsonParser jsonParser,
                                   DeserializationContext deserializationContext)
            throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        SupplierStorage supplierStorage = new SupplierStorage();
        supplierStorage.setId(node.get("id").asLong());
        supplierStorage.setFreeSpace(node.get("freeSpace").asInt());
        supplierStorage.setTotalSpace(node.get("totalSpace").asInt());

        List<String> countStringList = node.findValuesAsText("count");
        List<String> priceStringList = node.findValuesAsText("price");

        Set<SupplierStorageProduct> supplierStorageProductSet = new HashSet<>();
        for(int iterator = 0; iterator < countStringList.size(); iterator++) {
            SupplierStorageProduct supplierStorageProduct = new SupplierStorageProduct();
            supplierStorageProduct.setCount(Integer.valueOf(countStringList.get(iterator)));
            supplierStorageProduct.setPrice(new BigDecimal(priceStringList.get(iterator)));
            supplierStorageProductSet.add(supplierStorageProduct);
        }

        supplierStorage.setProducts(supplierStorageProductSet);
        return supplierStorage;
    }
}