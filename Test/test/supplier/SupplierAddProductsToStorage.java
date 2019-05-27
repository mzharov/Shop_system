package test.supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import test.config.TestDataServiceConfig;
import ts.tsc.system.controller.status.ErrorStatus;
import ts.tsc.system.entity.supplier.Supplier;
import ts.tsc.system.entity.supplier.SupplierStorage;
import ts.tsc.system.entity.supplier.SupplierStorageProduct;
import ts.tsc.system.service.storage.manager.StorageServiceInterface;
import ts.tsc.system.service.supplier.SupplierInterface;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Тестирование добавления товаров на склад
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {TestDataServiceConfig.class})
@WebAppConfiguration
public class SupplierAddProductsToStorage {

    @Autowired
    SupplierInterface supplierService;
    @Autowired
    StorageServiceInterface<Supplier, SupplierStorage, Long> supplierStorageService;

    /**
     * Тестирование на валидных данных
     * Сравниваются ожидаемые данные и реальные, которые сохраняются в БД после выполнения запроса
     * по добавлению твоаров на склад
     */
    @Test
    public void addProductsToStorage() {
        Long supplierStorageID = 3L;
        List<Long> productIDList = Arrays.asList(1L, 3L);
        List<Integer> countList = Arrays.asList(10, 10);
        List<String> priceList = Arrays.asList("10", "10");

        Optional<SupplierStorage> supplierStorageOptional =
                supplierStorageService.findById(supplierStorageID);
        assertTrue(supplierStorageOptional.isPresent());
        SupplierStorage supplierStorage = supplierStorageOptional.get();

        //Сохранение данных о товарах до добавления
        List<SupplierStorageProduct> supplierStorageProductList = supplierStorage
                .getProducts()
                .stream()
                .sorted(Comparator.comparing(item->item.getPrimaryKey().getProduct().getId()))
                .collect(Collectors.toList());
        assertTrue(supplierStorageProductList.size() > 0);

        List<Long> productExpectedIDList = new LinkedList<>();
        List<Integer> productExpectedCountList = new LinkedList<>();
        List<BigDecimal> productExpectedPriceList = new LinkedList<>();

        for(SupplierStorageProduct supplierStorageProduct : supplierStorageProductList) {
            productExpectedIDList.add(supplierStorageProduct.getPrimaryKey().getProduct().getId());
            productExpectedCountList.add(supplierStorageProduct.getCount());
            productExpectedPriceList.add(supplierStorageProduct.getPrice());
        }

        //Добавление в списки данных, которые будут добавлены после выполнения запроса
        for(int iterator = 0; iterator < productIDList.size(); iterator++) {
            int index = productExpectedIDList.indexOf(productIDList.get(iterator));
            if(index >= 0) {
                productExpectedCountList
                        .set(index, productExpectedCountList.get(index)+countList.get(iterator));
                productExpectedPriceList.set(index,
                        new BigDecimal(priceList.get(iterator)).setScale(5, RoundingMode.HALF_UP));
            } else {
                productExpectedIDList.add(productIDList.get(iterator));
                productExpectedCountList.add(countList.get(iterator));
                productExpectedPriceList.add(new BigDecimal(priceList.get(iterator))
                        .setScale(5, BigDecimal.ROUND_HALF_UP));
            }
        }

        //Получение списка реальных данных
        ResponseEntity<?> responseEntity = supplierService
                .addProductsToStorage(supplierStorageID, productIDList, countList, priceList);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        supplierStorageOptional =
                supplierStorageService.findById(supplierStorageID);
        assertTrue(supplierStorageOptional.isPresent());
        supplierStorage = supplierStorageOptional.get();

        supplierStorageProductList = supplierStorage
                .getProducts()
                .stream()
                .sorted(Comparator.comparing(item->item.getPrimaryKey().getProduct().getId()))
                .collect(Collectors.toList());
        assertTrue(supplierStorageProductList.size() > 0);

        List<Long> productRealIDList = new LinkedList<>();
        List<Integer> productRealCountIDList = new LinkedList<>();
        List<BigDecimal> productRealPriceIDList = new LinkedList<>();

        for(SupplierStorageProduct supplierStorageProduct : supplierStorageProductList) {
            productRealIDList.add(supplierStorageProduct.getPrimaryKey().getProduct().getId());
            productRealCountIDList.add(supplierStorageProduct.getCount());
            productRealPriceIDList.add(supplierStorageProduct.getPrice());
        }

        assertEquals(productExpectedIDList.size(), productRealIDList.size());

        //Сравнение ожидаемых и реальных результатов
        for(int iterator = 0; iterator < productExpectedCountList.size(); iterator++) {
            int index = productRealIDList.indexOf(productExpectedIDList.get(iterator));
            assertTrue(index >= 0);
            assertEquals(productExpectedCountList.get(iterator), productRealCountIDList.get(index));
            assertEquals(productExpectedPriceList.get(iterator), productRealPriceIDList.get(index));
        }

    }

    /**
     * Тестирование на неверных данных
     */
    @Test
    public void wrongParameters() {
        //Несуществующий склад
        long supplierStorageID = 4L;
        List<Long> productIDList = Arrays.asList(1L, 3L);
        List<Integer> countList = Arrays.asList(10, 10);
        List<String> priceList = Arrays.asList(String.valueOf(10), String.valueOf(10));

        ResponseEntity<?> responseEntity =
                supplierService.addProductsToStorage(supplierStorageID, productIDList, countList, priceList);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        //Разное количество элементов в списках
        supplierStorageID = 4L;
        productIDList = Arrays.asList(1L, 3L, 5L);
        countList = Arrays.asList(10, 10);
        priceList = Arrays.asList(String.valueOf(10), String.valueOf(10));

        responseEntity =
                supplierService.addProductsToStorage(supplierStorageID, productIDList, countList, priceList);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(ErrorStatus.WRONG_NUMBER_OF_PARAMETERS, responseEntity.getBody());

        //Разное количество элементов в списках
        supplierStorageID = 4L;
        productIDList = Arrays.asList(1L, 3L);
        countList = Arrays.asList(10, 10);
        priceList = Arrays.asList(String.valueOf(10), String.valueOf(10), String.valueOf(10));

        responseEntity =
                supplierService.addProductsToStorage(supplierStorageID, productIDList, countList, priceList);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(ErrorStatus.WRONG_NUMBER_OF_PARAMETERS, responseEntity.getBody());

        //Разное количество элементов в списках
        supplierStorageID = 4L;
        productIDList = Arrays.asList(1L, 3L);
        countList = Arrays.asList(10, 10);
        priceList = Arrays.asList("10", "10,00");

        responseEntity =
                supplierService.addProductsToStorage(supplierStorageID, productIDList, countList, priceList);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(ErrorStatus.NUMBER_FORMAT_EXCEPTION, responseEntity.getBody());
    }
}
