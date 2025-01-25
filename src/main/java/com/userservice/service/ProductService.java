package com.userservice.service;

import com.userservice.exception.ProductException;
import com.userservice.user.Product;
import com.userservice.user.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final String GET_PRODUCT_BY_ID = "https://dummyjson.com/products/";

    public List<Product> fetchProducts() {
        ProductResponse res = this.template.getForObject(API_URL, ProductResponse.class);
        if (res != null && res.products() != null) {
            return res.products().parallelStream().filter(product -> product.rating() > 3.5 &&
                    product.stock() > 0).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public Product getproductById(long id){
        Product product = this.template.getForObject(GET_PRODUCT_BY_ID+id,Product.class);
        if (product != null) {
            throw new ProductException("Product doesn't exist with id "+id);
        }
        return product;
    }

}
