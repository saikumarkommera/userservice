package com.userservice.service;

import com.userservice.exception.ProductException;
import com.userservice.user.Product;
import com.userservice.user.ProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final RestTemplate template;
    private final String API_URL = "https://dummyjson.com/products";
    private final String GET_PRODUCT_BY_ID = API_URL+"/";
    private final RedisService redis;

    @Retry(name = "userservice", fallbackMethod = "getAllProductsFallback")
    @CircuitBreaker(name = "userserivce", fallbackMethod = "getAllProductsFallback")
    public List<Product> fetchProducts() {
        Object prods = this.redis.get("products");
        if(prods!=null){
            log.info("Gor products from cache");
            return (List<Product>)prods;
        }
        ResponseEntity<ProductResponse> response  = this.template.getForEntity(API_URL, ProductResponse.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody()!=null) {
            List<Product> products =  response.getBody().products().parallelStream().filter(product -> product.rating() > 3.5 &&
                    product.stock() > 0).collect(Collectors.toList());
            this.redis.set("products",products,3000l);
            return products;
        }else{
            return Collections.emptyList();
        }
    }

    @Retry(name = "userservice", fallbackMethod = "getProductFallback")
    @CircuitBreaker(name = "userserivce", fallbackMethod = "getProductFallback")
    public Product getproductById(long id){
        ResponseEntity<Product> response = this.template.getForEntity(GET_PRODUCT_BY_ID+id,Product.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody()!=null) {
            log.info("Fetched product details from api : "+response.getBody());
            return response.getBody();
        } else {
            log.info("Got error response from API : "+response.getBody().message());
            throw new ProductException("Unexpected error while fetching product with id "+id);
        }
    }

    public List<Product> getAllProductsFallback(Exception ex) {
        log.error("Fallback triggered for fetchProducts: {}", ex.getMessage());
        return Collections.emptyList();
    }

    public Product getProductFallback(long id, Exception ex) {
        log.error("Fallback triggered for getProductById (id: {}): {}", id, ex.getMessage());
        return null;
    }

}
