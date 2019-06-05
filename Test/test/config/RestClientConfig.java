package test.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import test.deserializer.ShopStorageDeserializer;
import test.deserializer.ShopStorageProductDeserializer;
import ts.tsc.system.entity.shop.ShopStorage;
import ts.tsc.system.entity.shop.ShopStorageProduct;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestClientConfig {

    @Autowired ApplicationContext ctx;


    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter
                = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper());
        return mappingJackson2HttpMessageConverter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(ShopStorage.class, new ShopStorageDeserializer());
        simpleModule.addDeserializer(ShopStorageProduct.class, new ShopStorageProductDeserializer());
        objMapper.registerModule(simpleModule);
        return objMapper;
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory httpRequestFactory() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = HttpClientBuilder.create().build();
        httpRequestFactory.setHttpClient(httpClient);
        return httpRequestFactory;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
        List<HttpMessageConverter<?>> mcv = new ArrayList<>();
        mcv.add(mappingJackson2HttpMessageConverter());
        restTemplate.setMessageConverters(mcv);
        return restTemplate;
    }
}