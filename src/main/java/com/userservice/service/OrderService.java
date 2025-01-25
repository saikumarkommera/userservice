package com.userservice.service;

import com.userservice.exception.ProductException;
import com.userservice.repository.OrderRepository;
import com.userservice.user.Order;
import com.userservice.user.OrderRequest;
import com.userservice.user.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final ProductService productService;
    private final ExecutorService exeService = Executors.newFixedThreadPool(5);

    public Order placeOrder(OrderRequest orderRequest) {
        final CompletableFuture<Order> futureOrder = new CompletableFuture<>();
        exeService.submit(()->{
                Order newOrder = processOrder(orderRequest);
                futureOrder.complete(newOrder);
        });
        try {
            return futureOrder.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Order processOrder(OrderRequest orderRequest) {
        log.info("processing order request : "+orderRequest.toString());
        Product product = this.productService.getproductById(orderRequest.productId());
        if(product.stock() < orderRequest.quantity()){
            log.info("Available product quantity is less than required quantity");
            throw new ProductException("The availabel stock for product "+orderRequest.productId()+" is "+product.stock());
        }
        Order order = new Order();
        order.setQuantity(orderRequest.quantity());
        order.setProductId(orderRequest.productId());
        order.setUserId(orderRequest.userId());
        order.setOrderDate(LocalDateTime.now());
        order.setOrderAmount(product.price() * orderRequest.quantity());
        Order newOrder = this.repository.save(order);
        log.info("New order placed with id :"+newOrder.getId());
        return newOrder;
    }
}
