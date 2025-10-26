package com.nivakaran.bookingservice.client;

import com.nivakaran.bookingservice.dto.RoomResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PatchExchange;

public interface HotelClient {
    Logger log = LoggerFactory.getLogger(HotelClient.class);

    @GetExchange("/api/hotel/rooms/{id}")
    @CircuitBreaker(name = "hotel", fallbackMethod = "fallbackGetRoomById")
    @Retry(name = "hotel")
    RoomResponse getRoomById(@PathVariable String id);

    @GetExchange("/api/hotel/rooms/{id}/availability")
    @CircuitBreaker(name = "hotel", fallbackMethod = "fallbackRoomAvailability")
    @Retry(name = "hotel")
    boolean isRoomAvailable(@PathVariable String id);

    @GetExchange("/api/hotel/tables/{id}/availability")
    @CircuitBreaker(name = "hotel", fallbackMethod = "fallbackTableAvailability")
    @Retry(name = "hotel")
    boolean isTableAvailable(@PathVariable String id);

    @PatchExchange("/api/hotel/rooms/{id}/status")
    @CircuitBreaker(name = "hotel", fallbackMethod = "fallbackUpdateRoomStatus")
    @Retry(name = "hotel")
    void updateRoomStatus(@PathVariable String id, @RequestParam String status);

    @PatchExchange("/api/hotel/tables/{id}/status")
    @CircuitBreaker(name = "hotel", fallbackMethod = "fallbackUpdateTableStatus")
    @Retry(name = "hotel")
    void updateTableStatus(@PathVariable String id, @RequestParam String status);

    // Fallback methods
    default RoomResponse fallbackGetRoomById(String id, Throwable throwable) {
        log.error("Cannot fetch room details for id: {}, error: {}", id, throwable.getMessage());
        throw new RuntimeException("Cannot fetch room details for id: " + id + ", error: " + throwable.getMessage());
    }

    default boolean fallbackRoomAvailability(String id, Throwable throwable) {
        log.error("Cannot check room availability for id: {}, error: {}", id, throwable.getMessage());
        return false;
    }

    default boolean fallbackTableAvailability(String id, Throwable throwable) {
        log.error("Cannot check table availability for id: {}, error: {}", id, throwable.getMessage());
        return false;
    }

    default void fallbackUpdateRoomStatus(String id, String status, Throwable throwable) {
        log.error("Cannot update room status for id: {}, error: {}", id, throwable.getMessage());
    }

    default void fallbackUpdateTableStatus(String id, String status, Throwable throwable) {
        log.error("Cannot update table status for id: {}, error: {}", id, throwable.getMessage());
    }
}