package system.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import system.entities.Shop;
import system.services.ShopService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/shop")
public class ShopController {

    final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @Autowired
    ShopService shopService;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public List<Shop> getShops() {
        Iterable<Shop> iterable = shopService.findAll();
        List<Shop> shops = new ArrayList<>();
        iterable.forEach(shops::add);
        return shops;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Shop findShopById(@PathVariable Long id) {
        return shopService.findById(id);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public Shop create(@RequestBody Shop shop) {
        shopService.save(shop);
        return shop;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void update(@RequestBody Shop shop, @PathVariable Long id) {
        shopService.save(shop);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable Long id) {
        shopService.delete(shopService.findById(id));
    }
}

