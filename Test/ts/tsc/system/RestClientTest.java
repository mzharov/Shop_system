package ts.tsc.system;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import ts.tsc.system.serialization.Shops;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by iuliana.cosmina on 6/17/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RestClientConfig.class})
public class RestClientTest {

    final Logger logger = LoggerFactory.getLogger(RestClientTest.class);
    private static final String URL_GET_ALL_SHOPS = "http://localhost:8080/trade/shop/list";


    @Autowired
    RestTemplate restTemplate;

    @Before
    public void setUp() {
        assertNotNull(restTemplate);
    }

    @Test
    public void testFindAll() {
        logger.info("--> Testing retrieve all shops");
        //Shops shops = restTemplate.getForObject(URL_GET_ALL_SHOPS, Shops.class);
        //assertTrue(shops.getShops().size() == 1);
        //listSingers(shops);
    }


    private void listSingers(Shops singers) {
        singers.getShops().forEach(s -> logger.info(s.getShopID() + " " + s.getName() + " " + s.getBudget()));
    }
}