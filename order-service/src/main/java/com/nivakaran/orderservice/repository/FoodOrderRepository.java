package com.nivakaran.orderservice.repository;

import com.nivakaran.orderservice.model.FoodOrder;
import com.nivakaran.orderservice.model.OrderStatus;
import com.nivakaran.orderservice.model.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {
    Optional<FoodOrder> findByOrderNumber(String orderNumber);
    List<FoodOrder> findByGuestEmail(String guestEmail);
    List<FoodOrder> findByStatus(OrderStatus status);
    List<FoodOrder> findByOrderType(OrderType orderType);
    List<FoodOrder> findByTableId(String tableId);
    List<FoodOrder> findByRoomNumber(String roomNumber);
}