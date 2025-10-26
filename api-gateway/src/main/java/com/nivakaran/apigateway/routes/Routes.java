package com.nivakaran.apigateway.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;

@Configuration
public class Routes {

    @Value("${hotel.service.url}")
    private String hotelServiceUrl;

    @Value("${booking.service.url}")
    private String bookingServiceUrl;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    @Value("${restaurant.service.url}")
    private String restaurantServiceUrl;

    @Value("${guest.service.url}")
    private String guestServiceUrl;

    @Value("${staff.service.url}")
    private String staffServiceUrl;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    // Hotel Service Routes
    @Bean
    public RouterFunction<ServerResponse> hotelServiceRoute() {
        return GatewayRouterFunctions.route("hotel_service")
                .route(RequestPredicates.path("/api/hotel/**"), HandlerFunctions.http(hotelServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("hotelServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> hotelServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("hotel_service_swagger")
                .route(RequestPredicates.path("/aggregate/hotel-service/v3/api-docs"), HandlerFunctions.http(hotelServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("hotelServiceSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
    }

    // Booking Service Routes
    @Bean
    public RouterFunction<ServerResponse> bookingServiceRoute() {
        return GatewayRouterFunctions.route("booking_service")
                .route(RequestPredicates.path("/api/booking/**"), HandlerFunctions.http(bookingServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("bookingServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> bookingServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("booking_service_swagger")
                .route(RequestPredicates.path("/aggregate/booking-service/v3/api-docs"), HandlerFunctions.http(bookingServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("bookingServiceSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
    }

    // Order Service Routes
    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return GatewayRouterFunctions.route("order_service")
                .route(RequestPredicates.path("/api/order/**"), HandlerFunctions.http(orderServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("order_service_swagger")
                .route(RequestPredicates.path("/aggregate/order-service/v3/api-docs"), HandlerFunctions.http(orderServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
    }

    // Restaurant Service Routes
    @Bean
    public RouterFunction<ServerResponse> restaurantServiceRoute() {
        return GatewayRouterFunctions.route("restaurant_service")
                .route(RequestPredicates.path("/api/restaurant/**"), HandlerFunctions.http(restaurantServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("restaurantServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> restaurantServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("restaurant_service_swagger")
                .route(RequestPredicates.path("/aggregate/restaurant-service/v3/api-docs"), HandlerFunctions.http(restaurantServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("restaurantServiceSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
    }

    // Guest Service Routes
    @Bean
    public RouterFunction<ServerResponse> guestServiceRoute() {
        return GatewayRouterFunctions.route("guest_service")
                .route(RequestPredicates.path("/api/guest/**"), HandlerFunctions.http(guestServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("guestServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> guestServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("guest_service_swagger")
                .route(RequestPredicates.path("/aggregate/guest-service/v3/api-docs"), HandlerFunctions.http(guestServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("guestServiceSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
    }

    // Staff Service Routes
    @Bean
    public RouterFunction<ServerResponse> staffServiceRoute() {
        return GatewayRouterFunctions.route("staff_service")
                .route(RequestPredicates.path("/api/staff/**"), HandlerFunctions.http(staffServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("staffServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> staffServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("staff_service_swagger")
                .route(RequestPredicates.path("/aggregate/staff-service/v3/api-docs"), HandlerFunctions.http(staffServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("staffServiceSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
    }

    // Payment Service Routes
    @Bean
    public RouterFunction<ServerResponse> paymentServiceRoute() {
        return GatewayRouterFunctions.route("payment_service")
                .route(RequestPredicates.path("/api/payment/**"), HandlerFunctions.http(paymentServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("paymentServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("payment_service_swagger")
                .route(RequestPredicates.path("/aggregate/payment-service/v3/api-docs"), HandlerFunctions.http(paymentServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("paymentServiceSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return route("fallbackRoute")
                .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Service Unavailable, please try again later"))
                .build();
    }
}