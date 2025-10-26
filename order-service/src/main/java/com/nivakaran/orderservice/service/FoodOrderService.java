package com.nivakaran.orderservice.service;

import com.nivakaran.orderservice.client.RestaurantClient;
import com.nivakaran.orderservice.dto.FoodOrderRequest;
import com.nivakaran.orderservice.dto.FoodOrderResponse;
import com.nivakaran.orderservice.dto.OrderItemResponse;
import com.nivakaran.orderservice.event.OrderPlacedEvent;
import com.nivakaran.orderservice.model.FoodOrder;
import com.nivakaran.orderservice.model.OrderItem;
import com.nivakaran.orderservice.model.OrderStatus;
import com.nivakaran.orderservice.model.OrderType;
import com.nivakaran.orderservice.repository.FoodOrderRepository;
import com.nivakaran.orderservice.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodOrderService {

    private final FoodOrderRepository foodOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantClient restaurantClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10% tax
    private static final BigDecimal SERVICE_CHARGE_RATE = new BigDecimal("0.05"); // 5% service charge

    @Transactional
    public FoodOrderResponse createOrder(FoodOrderRequest request) {
        log.info("Creating new food order for guest: {}", request.guestName());

        // Validate order type specific fields
        validateOrderRequest(request);

        // Check menu item availability
        for (var item : request.items()) {
            boolean isAvailable = restaurantClient.isMenuItemAvailable(item.menuItemId());
            if (!isAvailable) {
                throw new RuntimeException("Menu item " + item.menuItemId() + " is not available");
            }
        }

        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String orderedBy = getCurrentUsername();

        // Calculate totals (In production, fetch actual prices from restaurant service)
        BigDecimal subtotal = calculateSubtotal(request);
        BigDecimal taxAmount = subtotal.multiply(TAX_RATE);
        BigDecimal serviceCharge = subtotal.multiply(SERVICE_CHARGE_RATE);
        BigDecimal totalAmount = subtotal.add(taxAmount).add(serviceCharge);

        FoodOrder order = FoodOrder.builder()
                .orderNumber(orderNumber)
                .orderType(request.orderType())
                .tableId(request.tableId())
                .roomNumber(request.roomNumber())
                .guestName(request.guestName())
                .guestEmail(request.guestEmail())
                .guestPhone(request.guestPhone())
                .subtotal(subtotal)
                .taxAmount(taxAmount)
                .serviceCharge(serviceCharge)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .specialInstructions(request.specialInstructions())
                .orderDateTime(LocalDateTime.now())
                .estimatedDeliveryTime(calculateEstimatedDeliveryTime(request.orderType()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderedBy(orderedBy)
                .build();

        FoodOrder savedOrder = foodOrderRepository.save(order);

        // Save order items
        List<OrderItem> orderItems = request.items().stream()
                .map(item -> createOrderItem(orderNumber, item))
                .collect(Collectors.toList());
        orderItemRepository.saveAll(orderItems);

        // Send order placed event
        sendOrderPlacedEvent(savedOrder);

        log.info("Food order created successfully: {}", orderNumber);
        return mapToResponse(savedOrder, orderItems);
    }

    public List<FoodOrderResponse> getAllOrders() {
        log.info("Fetching all food orders");
        return foodOrderRepository.findAll().stream()
                .map(this::mapToResponseWithItems)
                .collect(Collectors.toList());
    }

    public FoodOrderResponse getOrderById(Long id) {
        log.info("Fetching order by id: {}", id);
        FoodOrder order = foodOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return mapToResponseWithItems(order);
    }

    public FoodOrderResponse getOrderByOrderNumber(String orderNumber) {
        log.info("Fetching order by number: {}", orderNumber);
        FoodOrder order = foodOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with number: " + orderNumber));
        return mapToResponseWithItems(order);
    }

    public List<FoodOrderResponse> getOrdersByGuestEmail(String email) {
        log.info("Fetching orders for guest: {}", email);
        return foodOrderRepository.findByGuestEmail(email).stream()
                .map(this::mapToResponseWithItems)
                .collect(Collectors.toList());
    }

    public List<FoodOrderResponse> getOrdersByStatus(OrderStatus status) {
        log.info("Fetching orders by status: {}", status);
        return foodOrderRepository.findByStatus(status).stream()
                .map(this::mapToResponseWithItems)
                .collect(Collectors.toList());
    }

    public List<FoodOrderResponse> getOrdersByType(OrderType type) {
        log.info("Fetching orders by type: {}", type);
        return foodOrderRepository.findByOrderType(type).stream()
                .map(this::mapToResponseWithItems)
                .collect(Collectors.toList());
    }

    public List<FoodOrderResponse> getOrdersByTable(String tableId) {
        log.info("Fetching orders for table: {}", tableId);
        return foodOrderRepository.findByTableId(tableId).stream()
                .map(this::mapToResponseWithItems)
                .collect(Collectors.toList());
    }

    public List<FoodOrderResponse> getOrdersByRoom(String roomNumber) {
        log.info("Fetching orders for room: {}", roomNumber);
        return foodOrderRepository.findByRoomNumber(roomNumber).stream()
                .map(this::mapToResponseWithItems)
                .collect(Collectors.toList());
    }

    @Transactional
    public FoodOrderResponse updateOrderStatus(String orderNumber, OrderStatus status) {
        log.info("Updating order status for: {} to: {}", orderNumber, status);

        FoodOrder order = foodOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with number: " + orderNumber));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        if (status == OrderStatus.DELIVERED) {
            order.setActualDeliveryTime(LocalDateTime.now());
        }

        FoodOrder updatedOrder = foodOrderRepository.save(order);
        log.info("Order status updated successfully");

        return mapToResponseWithItems(updatedOrder);
    }

    @Transactional
    public void cancelOrder(String orderNumber) {
        log.info("Cancelling order: {}", orderNumber);

        FoodOrder order = foodOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with number: " + orderNumber));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel order that is already delivered or completed");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        foodOrderRepository.save(order);

        log.info("Order cancelled successfully");
    }

    private void validateOrderRequest(FoodOrderRequest request) {
        if (request.orderType() == OrderType.DINE_IN && request.tableId() == null) {
            throw new RuntimeException("Table ID is required for dine-in orders");
        }
        if (request.orderType() == OrderType.ROOM_SERVICE && request.roomNumber() == null) {
            throw new RuntimeException("Room number is required for room service orders");
        }
    }

    private BigDecimal calculateSubtotal(FoodOrderRequest request) {
        // In production, fetch actual prices from restaurant service
        // For now, using dummy price of 15.00 per item
        return request.items().stream()
                .map(item -> new BigDecimal("15.00").multiply(new BigDecimal(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private LocalDateTime calculateEstimatedDeliveryTime(OrderType orderType) {
        int estimatedMinutes = switch (orderType) {
            case DINE_IN -> 30;
            case ROOM_SERVICE -> 45;
            case TAKEAWAY -> 20;
        };
        return LocalDateTime.now().plusMinutes(estimatedMinutes);
    }

    private OrderItem createOrderItem(String orderNumber, com.nivakaran.orderservice.dto.OrderItemRequest itemRequest) {
        // In production, fetch actual menu item details from restaurant service
        BigDecimal unitPrice = new BigDecimal("15.00"); // Dummy price
        BigDecimal totalPrice = unitPrice.multiply(new BigDecimal(itemRequest.quantity()));

        return OrderItem.builder()
                .orderNumber(orderNumber)
                .menuItemId(itemRequest.menuItemId())
                .menuItemName("Menu Item " + itemRequest.menuItemId()) // Fetch from restaurant service
                .quantity(itemRequest.quantity())
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .specialInstructions(itemRequest.specialInstructions())
                .build();
    }

    private void sendOrderPlacedEvent(FoodOrder order) {
        OrderPlacedEvent event = OrderPlacedEvent.newBuilder()
                .setOrderNumber(order.getOrderNumber())
                .setGuestEmail(order.getGuestEmail())
                .setGuestName(order.getGuestName())
                .setOrderType(order.getOrderType().name())
                .setTotalAmount(order.getTotalAmount().toString())
                .build();

        kafkaTemplate.send("order-placed", event);
        log.info("Order placed event sent for: {}", order.getOrderNumber());
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "system";
    }

    private FoodOrderResponse mapToResponseWithItems(FoodOrder order) {
        List<OrderItem> items = orderItemRepository.findByOrderNumber(order.getOrderNumber());
        return mapToResponse(order, items);
    }

    private FoodOrderResponse mapToResponse(FoodOrder order, List<OrderItem> items) {
        List<OrderItemResponse> itemResponses = items.stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getMenuItemId(),
                        item.getMenuItemName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice(),
                        item.getSpecialInstructions()
                ))
                .collect(Collectors.toList());

        return new FoodOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getOrderType(),
                order.getTableId(),
                order.getRoomNumber(),
                order.getGuestName(),
                order.getGuestEmail(),
                order.getGuestPhone(),
                order.getSubtotal(),
                order.getTaxAmount(),
                order.getServiceCharge(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getSpecialInstructions(),
                order.getOrderDateTime(),
                order.getEstimatedDeliveryTime(),
                order.getActualDeliveryTime(),
                itemResponses
        );
    }
}