package com.nivakaran.orderservice.controller;

import com.nivakaran.orderservice.dto.FoodOrderRequest;
import com.nivakaran.orderservice.dto.FoodOrderResponse;
import com.nivakaran.orderservice.model.OrderStatus;
import com.nivakaran.orderservice.model.OrderType;
import com.nivakaran.orderservice.service.FoodOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class FoodOrderController {

    private final FoodOrderService foodOrderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('customer') or hasRole('staff') or hasRole('admin')")
    public FoodOrderResponse createOrder(@Valid @RequestBody FoodOrderRequest request) {
        return foodOrderService.createOrder(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<FoodOrderResponse> getAllOrders() {
        return foodOrderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public FoodOrderResponse getOrderById(@PathVariable Long id) {
        return foodOrderService.getOrderById(id);
    }

    @GetMapping("/number/{orderNumber}")
    public FoodOrderResponse getOrderByOrderNumber(@PathVariable String orderNumber) {
        return foodOrderService.getOrderByOrderNumber(orderNumber);
    }

    @GetMapping("/guest/{email}")
    public List<FoodOrderResponse> getOrdersByGuestEmail(@PathVariable String email) {
        return foodOrderService.getOrdersByGuestEmail(email);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<FoodOrderResponse> getOrdersByStatus(@PathVariable OrderStatus status) {
        return foodOrderService.getOrdersByStatus(status);
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<FoodOrderResponse> getOrdersByType(@PathVariable OrderType type) {
        return foodOrderService.getOrdersByType(type);
    }

    @GetMapping("/table/{tableId}")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<FoodOrderResponse> getOrdersByTable(@PathVariable String tableId) {
        return foodOrderService.getOrdersByTable(tableId);
    }

    @GetMapping("/room/{roomNumber}")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<FoodOrderResponse> getOrdersByRoom(@PathVariable String roomNumber) {
        return foodOrderService.getOrdersByRoom(roomNumber);
    }

    @PatchMapping("/{orderNumber}/status")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public FoodOrderResponse updateOrderStatus(
            @PathVariable String orderNumber,
            @RequestParam OrderStatus status) {
        return foodOrderService.updateOrderStatus(orderNumber, status);
    }

    @DeleteMapping("/{orderNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable String orderNumber) {
        foodOrderService.cancelOrder(orderNumber);
    }
}