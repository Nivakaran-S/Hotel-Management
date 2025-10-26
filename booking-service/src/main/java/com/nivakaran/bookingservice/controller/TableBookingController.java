package com.nivakaran.bookingservice.controller;

import com.nivakaran.bookingservice.dto.TableBookingRequest;
import com.nivakaran.bookingservice.dto.TableBookingResponse;
import com.nivakaran.bookingservice.model.BookingStatus;
import com.nivakaran.bookingservice.service.TableBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking/tables")
@RequiredArgsConstructor
public class TableBookingController {

    private final TableBookingService tableBookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('customer') or hasRole('staff') or hasRole('admin')")
    public TableBookingResponse createTableBooking(@Valid @RequestBody TableBookingRequest request) {
        return tableBookingService.createTableBooking(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<TableBookingResponse> getAllTableBookings() {
        return tableBookingService.getAllTableBookings();
    }

    @GetMapping("/{id}")
    public TableBookingResponse getTableBookingById(@PathVariable Long id) {
        return tableBookingService.getTableBookingById(id);
    }

    @GetMapping("/number/{bookingNumber}")
    public TableBookingResponse getTableBookingByNumber(@PathVariable String bookingNumber) {
        return tableBookingService.getTableBookingByNumber(bookingNumber);
    }

    @GetMapping("/guest/{email}")
    public List<TableBookingResponse> getTableBookingsByGuestEmail(@PathVariable String email) {
        return tableBookingService.getTableBookingsByGuestEmail(email);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<TableBookingResponse> getTableBookingsByStatus(@PathVariable BookingStatus status) {
        return tableBookingService.getTableBookingsByStatus(status);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public TableBookingResponse updateBookingStatus(@PathVariable Long id, @RequestParam BookingStatus status) {
        return tableBookingService.updateBookingStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelTableBooking(@PathVariable Long id) {
        tableBookingService.cancelTableBooking(id);
    }
}