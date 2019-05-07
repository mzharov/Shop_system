import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import ts.tsc.system.entities.Shop;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RestClientConfig.class})
public class RestClientTest {

    private final static Logger logger = LoggerFactory.getLogger(RestClientTest.class);
    private static final String URL_GET_ALL_SHOPS = "http://localhost:8080";


    @Autowired
    RestTemplate restTemplate;

    @Before
    public void setUp() {
        assertNotNull(restTemplate);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFindAll() {
        logger.info("--> Testing retrieve all shops");
        List<Shop> shops =
                restTemplate.getForObject(URL_GET_ALL_SHOPS, List.class);
        assertEquals(1, shops.size());
        listSingers(shops);
    }


    private void listSingers(List<Shop> singers) {
        singers.forEach(s -> logger.info(s.getShopID() + " " + s.getName() + " " + s.getBudget()));
    }
}