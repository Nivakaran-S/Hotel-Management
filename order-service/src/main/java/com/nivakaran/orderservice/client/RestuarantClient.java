package com.nivakaran.orderservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

@Slf4j
public interface RestuarantClient {

    @GetExchange("/api/restaurant/menu/{id}/available")
    @CircuitBreaker(name = "restaurant", fallbackMethod = "fallbackMenuAvailability")
    @Retry(name = "restaurant")
    boolean isMenuItemAvailable(@PathVariable String id);

    default boolean fallbackMenuAvailability(String id, Throwable throwable) {
        log.error("Cannot check menu item availability for id: {}, error: {}", id, throwable.getMessage());
        return false;
    }
}