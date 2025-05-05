package com.userservice.repository;

import com.userservice.user.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
s
@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
}
