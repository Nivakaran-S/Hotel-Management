package com.nivakaran.bookingservice.controller;

import com.nivakaran.bookingservice.dto.RoomBookingRequest;
import com.nivakaran.bookingservice.dto.RoomBookingResponse;
import com.nivakaran.bookingservice.model.BookingStatus;
import com.nivakaran.bookingservice.service.RoomBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking/rooms")
@RequiredArgsConstructor
public class RoomBookingController {

    private final RoomBookingService roomBookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('customer') or hasRole('staff') or hasRole('admin')")
    public RoomBookingResponse createRoomBooking(@Valid @RequestBody RoomBookingRequest request) {
        return roomBookingService.createRoomBooking(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<RoomBookingResponse> getAllRoomBookings() {
        return roomBookingService.getAllRoomBookings();
    }

    @GetMapping("/{id}")
    public RoomBookingResponse getRoomBookingById(@PathVariable Long id) {
        return roomBookingService.getRoomBookingById(id);
    }

    @GetMapping("/number/{bookingNumber}")
    public RoomBookingResponse getRoomBookingByNumber(@PathVariable String bookingNumber) {
        return roomBookingService.getRoomBookingByNumber(bookingNumber);
    }

    @GetMapping("/guest/{email}")
    public List<RoomBookingResponse> getRoomBookingsByGuestEmail(@PathVariable String email) {
        return roomBookingService.getRoomBookingsByGuestEmail(email);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<RoomBookingResponse> getRoomBookingsByStatus(@PathVariable BookingStatus status) {
        return roomBookingService.getRoomBookingsByStatus(status);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public RoomBookingResponse updateBookingStatus(@PathVariable Long id, @RequestParam BookingStatus status) {
        return roomBookingService.updateBookingStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelRoomBooking(@PathVariable Long id) {
        roomBookingService.cancelRoomBooking(id);
    }
}