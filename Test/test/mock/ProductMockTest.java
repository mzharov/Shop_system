package test.mock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.ui.ExtendedModelMap;
import ts.tsc.system.controller.product.ProductController;
import ts.tsc.system.controller.response.BaseResponseBuilder;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.entity.product.Product;
import ts.tsc.system.service.product.ProductService;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ts.tsc.system.controller.status.ErrorStatus.ELEMENT_NOT_FOUND;

/**
 * Тестирование контроллера product
 */
public class ProductMockTest {

    private Product product;

    @Before
    public void setUp() {
        product = new Product();
        product.setId(1L);
        product.setCategory("drink");
        product.setName("milk");
    }

    /**
     * Тестирование добавления товара
     */
    @Test
    public void testAddProduct() {

        Product tmp = new Product();
        tmp.setCategory(product.getCategory());
        tmp.setName(tmp.getName());

        ProductService productService = mock(ProductService.class);
        when(productService.save(tmp)).thenAnswer((Answer<Product>) invocationOnMock -> product);

        ProductController productController = new ProductController(productService,
                new BaseResponseBuilder<>());

        System.out.println(productController.create(tmp).getBody());
        Product productCreate = (Product) productController.create(tmp).getBody();
        assertNotNull(productCreate);
        assertEquals(product.getId(), productCreate.getId());
        assertEquals(product.getCategory(), productCreate.getCategory());
        assertEquals(product.getName(), productCreate.getName());
    }

    /**
     * Тестирование поиска товара по id
     */
    @Test
    public void testGetProductByID() {
        ProductService productService = mock(ProductService.class);
        Mockito.when(productService.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(productService.findById(2L)).thenReturn(Optional.of(product));

        ProductController productController = new ProductController(productService,
                new BaseResponseBuilder<>());

        ExtendedModelMap uiModel = new ExtendedModelMap();
        uiModel.addAttribute("product", productController.findById(1L).getBody());
        Product productResult = (Product) uiModel.get("product");
        assertEquals(product, productResult);
    }

    /**
     * Тестирование ситуации с несуществующим в БД товаром
     */
    @Test
    public void testGetNotExistingProductByID() {
        ProductService productService = mock(ProductService.class);
        Mockito.when(productService.findById(2L)).thenReturn(Optional.empty());

        BaseResponseBuilder<Product> baseResponseBuilder = new BaseResponseBuilder<>();

        ProductController productController = new ProductController(productService, baseResponseBuilder);
        ExtendedModelMap uiModel = new ExtendedModelMap();
        uiModel.addAttribute("product", productController.findById(2L).getBody());

        ErrorStatus productResult = (ErrorStatus) uiModel.get("product");
        assertEquals(ELEMENT_NOT_FOUND, productResult);
    }
}
