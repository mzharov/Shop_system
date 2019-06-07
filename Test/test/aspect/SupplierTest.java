package test.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import ts.tsc.system.entity.supplier.Supplier;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SupplierTest extends CreateAndUpdateTest<Supplier>{

    private final static String PATH = "http://localhost:8080/app/supplier/";

    @Override
    String getPath() {
        return PATH;
    }

    @Override
    Supplier getEntity() {
        Supplier supplier = new Supplier();
        supplier.setName("name");
        return supplier;
    }

    @Override
    Supplier getCorruptedEntity() {
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("name");
        return supplier;
    }

    @Override
    void validateEntity(String json) throws IOException {
        Supplier supplier = new ObjectMapper().readValue(json, Supplier.class);
        assertNotNull(supplier);
        assertEquals(supplier.getName(), supplier.getName());
    }

    @Override
    void validateUpdated(String json, Long id) throws IOException {
        Supplier supplier = new ObjectMapper().readValue(json, Supplier.class);
        assertNotNull(supplier);
        assertEquals(supplier.getName(), supplier.getName());
        assertEquals(id, supplier.getId());
    }

    @Override
    Long getID() {
        return 1L;
    }
}