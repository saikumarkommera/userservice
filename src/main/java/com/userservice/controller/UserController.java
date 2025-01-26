package com.userservice.controller;

import com.userservice.service.OrderService;
import com.userservice.service.ProductService;
import com.userservice.service.UserService;
import com.userservice.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;
    private final ProductService prodService;
    private final OrderService orderService;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = prodService.fetchProducts();
        if(products.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }else{
            return ResponseEntity.ok(products);
        }
    }

    @PostMapping("/orders/place")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest request) {
        Order newOrder = this.orderService.placeOrder(request);
        return newOrder != null ? ResponseEntity.ok().body("Order got placed Successfully")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while creating order");
    }

    @PostMapping("/createUser")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO dto) {
        User user = this.userService.createNewUser(dto);
        return ResponseEntity.ok(mapper.toUserDTO(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") int id) {
        User user = this.userService.getUser(id);
        return ResponseEntity.ok(mapper.toUserDTO(user));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUserById(@PathVariable("id") int id, @RequestBody UserDTO dto) {
        User user = this.userService.updateUser(id, dto);
        return ResponseEntity.ok(mapper.toUserDTO(user));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") int id) {
        this.userService.deleteUser(id);
        return ResponseEntity.ok("User Deleted successfully..!");
    }
}
