package test.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import test.deserializer.SupplierStorageDeserializer;
import ts.tsc.system.entity.supplier.SupplierStorage;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SupplierStorageCreateTest extends CreateTest<SupplierStorage> {

    private final static String PATH = "http://localhost:8080/app/shop/storage/1";

    @Override
    String getPath() {
        return PATH;
    }

    @Override
    SupplierStorage getEntity() {
        SupplierStorage supplierStorage = new SupplierStorage();
        supplierStorage.setTotalSpace(10);
        supplierStorage.setFreeSpace(100);
        return supplierStorage;
    }

    @Override
    SupplierStorage getCorruptedEntity() {
        SupplierStorage supplierStorage = new SupplierStorage();
        supplierStorage.setId(1L);
        supplierStorage.setTotalSpace(10);
        supplierStorage.setFreeSpace(100);
        return supplierStorage;
    }

    @Override
    void validateEntity(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(SupplierStorage.class, new SupplierStorageDeserializer());
        objectMapper.registerModule(simpleModule);

        SupplierStorage supplierStorage = objectMapper.readValue(json, SupplierStorage.class);
        assertNotNull(supplierStorage);
        assertEquals(supplierStorage.getFreeSpace(), supplierStorage.getFreeSpace());
        assertEquals(supplierStorage.getTotalSpace(), supplierStorage.getTotalSpace());
    }
}
