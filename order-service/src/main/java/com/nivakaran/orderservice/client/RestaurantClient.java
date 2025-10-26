package com.nivakaran.orderservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface RestaurantClient {

    @GetExchange("/api/restaurant/menu/{id}/available")
    @CircuitBreaker(name = "restaurant", fallbackMethod = "fallbackMenuAvailability")
    @Retry(name = "restaurant")
    boolean isMenuItemAvailable(@PathVariable String id);

    default boolean fallbackMenuAvailability(String id, Throwable throwable) {
        System.out.println("Cannot check menu item availability for id: " + id + ", error: " + throwable.getMessage());
        return false;
    }
}