package com.userservice.service;

import com.userservice.exception.ProductException;
import com.userservice.exception.UserException;
import com.userservice.repository.OrderRepository;
import com.userservice.repository.UserRepository;
import com.userservice.user.Order;
import com.userservice.user.OrderRequest;
import com.userservice.user.Product;
import com.userservice.user.User;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final ProductService productService;
    private final ExecutorService exeService = Executors.newFixedThreadPool(5);
    private final UserRepository userRepository;


    public Order placeOrder(OrderRequest orderRequest) {
        // Check if the user exists
        this.userRepository.findById((int) orderRequest.userId())
                .orElseThrow(() -> new UserException("No User Found with id: " + orderRequest.userId()));
        CompletableFuture<Order> futureOrder = CompletableFuture.supplyAsync(() -> processOrder(orderRequest), exeService);
        try {
            return futureOrder.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Order processing was interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to process the order", e.getCause());
        }
    }

    private Order processOrder(OrderRequest orderRequest) {
        log.info("processing order request : " + orderRequest.toString());
        Product product = this.productService.getproductById(orderRequest.productId());
        if(product == null){
            Thread.currentThread().interrupt();
            throw new ProductException("Please check the prodcut id and try again..!");
        }
        else if (product.stock() < orderRequest.quantity()) {
            log.info("Available product quantity is less than required quantity");
            Thread.currentThread().interrupt();
            throw new ProductException("Sorry the current availabel stock for product is " + orderRequest.productId() + " is " + product.stock());
        }
        Order order = new Order();
        order.setQuantity(orderRequest.quantity());
        order.setProductId(orderRequest.productId());
        order.setUserId(orderRequest.userId());
        order.setOrderDate(LocalDateTime.now());
        order.setOrderAmount(product.price() * orderRequest.quantity());
        Order newOrder = this.repository.save(order);
        log.info("New order placed with id :" + newOrder.getId());
        return newOrder;
    }

    @PreDestroy
    public void shutdown() {
        log.info("Destroying executor service");
        exeService.shutdown();
        try {
            if (!exeService.awaitTermination(60, TimeUnit.SECONDS)) {
                exeService.shutdownNow();
            }
        } catch (InterruptedException e) {
            exeService.shutdownNow();
        }
    }
}
