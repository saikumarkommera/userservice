package com.userservice.service;

import com.userservice.exception.UserException;
import com.userservice.repository.OrderRepository;
import com.userservice.repository.UserRepository;
import com.userservice.user.Order;
import com.userservice.user.OrderRequest;
import com.userservice.user.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private ProductService productService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testPlaceOrder_Success() throws Exception {
        // Arrange
        OrderRequest request = new OrderRequest(1L, 1L, 2); // userId (long), productId (long), quantity
        Product product = new Product(1L, "Product A", "Description A", "Category A", 100.0, 4.5, 10, "In stock");
        Order savedOrder = new Order();
        savedOrder.setId(1l);
        savedOrder.setQuantity(request.quantity());
        savedOrder.setProductId(request.productId()); // Cast to int
        savedOrder.setUserId(request.userId()); // Cast to int
        savedOrder.setOrderDate(LocalDateTime.now());
        savedOrder.setOrderAmount(product.price() * request.quantity());

        when(userRepository.findById((int) request.userId())).thenReturn(Optional.of(new com.userservice.user.User())); // Cast to int
        when(productService.getproductById((int) request.productId())).thenReturn(product); // Cast to int
        when(repository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        Order result = orderService.placeOrder(request);

        // Assert
        assertNotNull(result);
        assertEquals(savedOrder.getId(), result.getId());
        assertEquals(savedOrder.getQuantity(), result.getQuantity());
        assertEquals(savedOrder.getProductId(), result.getProductId());
        assertEquals(savedOrder.getUserId(), result.getUserId());
        assertEquals(savedOrder.getOrderAmount(), result.getOrderAmount());
        verify(userRepository, times(1)).findById((int) request.userId()); // Cast to int
        verify(productService, times(1)).getproductById((int) request.productId()); // Cast to int
        verify(repository, times(1)).save(any(Order.class));
    }

    @Test
    void testPlaceOrder_UserNotFound() {
        // Arrange
        OrderRequest request = new OrderRequest(1L, 1L, 2); // userId (long), productId (long), quantity
        when(userRepository.findById((int) request.userId())).thenReturn(Optional.empty()); // Cast to int

        // Act & Assert
        UserException exception = assertThrows(UserException.class, () -> orderService.placeOrder(request));
        assertEquals("No User Found with id: " + request.userId(), exception.getMessage());
        verify(userRepository, times(1)).findById((int) request.userId()); // Cast to int
        verify(productService, never()).getproductById(anyInt());
        verify(repository, never()).save(any(Order.class));
    }


    @Test
    void testPlaceOrder_ExecutionException() {
        // Arrange
        OrderRequest request = new OrderRequest(1L, 1L, 2); // userId (long), productId (long), quantity
        Product product = new Product(1L, "Product A", "Description A", "Category A", 100.0, 4.5, 10, "In stock");
        when(userRepository.findById((int) request.userId())).thenReturn(Optional.of(new com.userservice.user.User())); // Cast to int
        when(productService.getproductById((int) request.productId())).thenReturn(product); // Cast to int
        when(repository.save(any(Order.class))).thenAnswer(invocation -> {
            throw new ExecutionException(new RuntimeException("Simulated execution exception"));
        });

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.placeOrder(request));
        assertEquals("Failed to process the order", exception.getMessage());
        verify(userRepository, times(1)).findById((int) request.userId()); // Cast to int
        verify(productService, times(1)).getproductById((int) request.productId()); // Cast to int
        verify(repository, times(1)).save(any(Order.class));
    }
}