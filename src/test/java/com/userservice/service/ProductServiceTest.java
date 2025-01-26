package com.userservice.service;

import com.userservice.user.Product;
import com.userservice.user.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private RedisService redis;

	@InjectMocks
	private ProductService productService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFetchProducts_FromCache() {
		// Arrange
		List<Product> cachedProducts = Arrays.asList(
				new Product(1L, "Product A", "Description A", "Category A", 100.0, 4.5, 10, "In stock"),
				new Product(2L, "Product B", "Description B", "Category B", 200.0, 4.0, 5, "Low stock")
		);
		when(redis.get("products")).thenReturn(cachedProducts);

		// Act
		List<Product> result = productService.fetchProducts();

		// Assert
		assertEquals(cachedProducts, result);
		verify(redis, times(1)).get("products");
		verify(restTemplate, never()).getForEntity(anyString(), eq(ProductResponse.class));
	}

	@Test
	void testFetchProducts_FromAPI() {
		// Arrange
		List<Product> apiProducts = Arrays.asList(
				new Product(1L, "Product A", "Description A", "Category A", 100.0, 4.5, 10, "In stock"),
				new Product(2L, "Product B", "Description B", "Category B", 200.0, 4.0, 5, "Low stock")
		);
		ProductResponse productResponse = new ProductResponse(apiProducts);
		when(redis.get("products")).thenReturn(null);
		when(restTemplate.getForEntity(anyString(), eq(ProductResponse.class)))
				.thenReturn(new ResponseEntity<>(productResponse, HttpStatus.OK));

		// Act
		List<Product> result = productService.fetchProducts();

		// Assert
		assertEquals(apiProducts, result);
		verify(redis, times(1)).get("products");
		verify(restTemplate, times(1)).getForEntity(anyString(), eq(ProductResponse.class));
		verify(redis, times(1)).set(eq("products"), eq(apiProducts), eq(3000L));
	}

	@Test
	void testFetchProducts_EmptyResponse() {
		// Arrange
		when(redis.get("products")).thenReturn(null);
		when(restTemplate.getForEntity(anyString(), eq(ProductResponse.class)))
				.thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

		// Act
		List<Product> result = productService.fetchProducts();

		// Assert
		assertEquals(Collections.emptyList(), result);
		verify(redis, times(1)).get("products");
		verify(restTemplate, times(1)).getForEntity(anyString(), eq(ProductResponse.class));
		verify(redis, never()).set(anyString(), anyList(), anyLong());
	}



	@Test
	void testGetProductById_Success() {
		// Arrange
		long productId = 1L;
		Product product = new Product(productId, "Product A", "Description A", "Category A", 100.0, 4.5, 10, "In stock");
		when(restTemplate.getForEntity(anyString(), eq(Product.class)))
				.thenReturn(new ResponseEntity<>(product, HttpStatus.OK));

		// Act
		Product result = productService.getproductById(productId);

		// Assert
		assertEquals(product, result);
		verify(restTemplate, times(1)).getForEntity(anyString(), eq(Product.class));
	}


}