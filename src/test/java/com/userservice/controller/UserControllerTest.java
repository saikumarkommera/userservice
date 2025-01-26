package com.userservice.controller;

import com.userservice.service.OrderService;
import com.userservice.service.ProductService;
import com.userservice.service.UserService;
import com.userservice.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper mapper;

    @Mock
    private ProductService prodService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testGetProducts_Success() {
        // Arrange
        List<Product> products = Arrays.asList(
                new Product(1L, "Product A", "Description A", "Category A", 100.0, 4.5, 10, "In stock"),
                new Product(2L, "Product B", "Description B", "Category B", 200.0, 4.0, 5, "Low stock")
        );
        when(prodService.fetchProducts()).thenReturn(products);

        // Act
        ResponseEntity<List<Product>> response = userController.getProducts();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
        verify(prodService, times(1)).fetchProducts();
    }

    @Test
    void testGetProducts_EmptyList() {
        // Arrange
        when(prodService.fetchProducts()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<Product>> response = userController.getProducts();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
        verify(prodService, times(1)).fetchProducts();
    }

    @Test
    void testPlaceOrder_Success() {
        // Arrange
        OrderRequest request = new OrderRequest(1L, 1L, 2); // userId, productId, quantity
        Order order = new Order();
        when(orderService.placeOrder(request)).thenReturn(order);

        // Act
        ResponseEntity<String> response = userController.placeOrder(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order got placed Successfully", response.getBody());
        verify(orderService, times(1)).placeOrder(request);
    }

    @Test
    void testPlaceOrder_Failure() {
        // Arrange
        OrderRequest request = new OrderRequest(1L, 1L, 2); // userId, productId, quantity
        when(orderService.placeOrder(request)).thenReturn(null);

        // Act
        ResponseEntity<String> response = userController.placeOrder(request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error while creating order", response.getBody());
        verify(orderService, times(1)).placeOrder(request);
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        UserDTO dto = new UserDTO(1l, "John Doe", "john@example.com");
        User user = new User(1l, "John Doe", "john@example.com");
        when(userService.createNewUser(dto)).thenReturn(user);
        when(mapper.toUserDTO(user)).thenReturn(dto);

        // Act
        ResponseEntity<UserDTO> response = userController.createUser(dto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(userService, times(1)).createNewUser(dto);
        verify(mapper, times(1)).toUserDTO(user);
    }

    @Test
    void testGetUserById_Success() {
        // Arrange
        long userId = 1;
        User user = new User(userId, "John Doe", "john@example.com");
        UserDTO dto = new UserDTO(userId, "John Doe", "john@example.com");
        when(userService.getUser((int) userId)).thenReturn(user);
        when(mapper.toUserDTO(user)).thenReturn(dto);

        // Act
        ResponseEntity<UserDTO> response = userController.getUserById((int) userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(userService, times(1)).getUser((int) userId);
        verify(mapper, times(1)).toUserDTO(user);
    }

    @Test
    void testUpdateUserById_Success() {
        // Arrange
        long userId = 1;
        UserDTO dto = new UserDTO(userId, "John Doe Updated", "john.updated@example.com");
        User user = new User(userId, "John Doe Updated", "john.updated@example.com");
        when(userService.updateUser((int) userId, dto)).thenReturn(user);
        when(mapper.toUserDTO(user)).thenReturn(dto);

        // Act
        ResponseEntity<UserDTO> response = userController.updateUserById((int) userId, dto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(userService, times(1)).updateUser((int) userId, dto);
        verify(mapper, times(1)).toUserDTO(user);
    }

    @Test
    void testDeleteUserById_Success() {
        // Arrange
        int userId = 1;
        doNothing().when(userService).deleteUser(userId);

        // Act
        ResponseEntity<String> response = userController.deleteUserById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User Deleted successfully..!", response.getBody());
        verify(userService, times(1)).deleteUser(userId);
    }
}