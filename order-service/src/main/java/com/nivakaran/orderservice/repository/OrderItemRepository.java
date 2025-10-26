package com.nivakaran.orderservice.repository;

import com.nivakaran.orderservice.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderNumber(String orderNumber);
    void deleteByOrderNumber(String orderNumber);
}