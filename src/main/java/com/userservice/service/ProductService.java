package com.userservice.service;

import com.userservice.exception.ProductException;
import com.userservice.user.Product;
import com.userservice.user.ProductResponse;
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

    public List<Product> fetchProducts() {
        ResponseEntity<ProductResponse> response  = this.template.getForEntity(API_URL, ProductResponse.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody()!=null) {
            return response.getBody().products().parallelStream().filter(product -> product.rating() > 3.5 &&
                    product.stock() > 0).collect(Collectors.toList());
        }else{
            return Collections.emptyList();
        }
    }

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

}
