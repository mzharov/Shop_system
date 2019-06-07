package test.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import ts.tsc.system.entity.product.Product;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProductTest extends CreateAndUpdateTest<Product>{

    private static final String PATH = "http://localhost:8080/app/product/";

    @Override
    String getPath() {
        return PATH;
    }

    @Override
    Product getEntity() {
        Product product = new Product();
        product.setName("name");
        product.setCategory("category");
        return product;
    }

    @Override
    Product getCorruptedEntity() {
        Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setCategory("category");
        return product;
    }

    @Override
    void validateEntity(String json) throws IOException {
        Product product = new ObjectMapper().readValue(json, Product.class);
        assertNotNull(product);
        assertEquals(product.getName(), product.getName());
    }

    @Override
    void validateUpdated(String json, Long id) throws IOException {
        Product product = new ObjectMapper().readValue(json, Product.class);
        assertNotNull(product);
        assertEquals(id, product.getId());
        assertEquals(product.getName(), product.getName());
    }

    @Override
    Long getID() {
        return 1L;
    }
}