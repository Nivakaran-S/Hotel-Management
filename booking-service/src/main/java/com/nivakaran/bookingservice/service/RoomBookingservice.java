package com.nivakaran.bookingservice.service;

import com.nivakaran.bookingservice.client.HotelClient;
import com.nivakaran.bookingservice.dto.RoomBookingRequest;
import com.nivakaran.bookingservice.dto.RoomBookingResponse;
import com.nivakaran.bookingservice.event.BookingConfirmedEvent;
import com.nivakaran.bookingservice.model.BookingStatus;
import com.nivakaran.bookingservice.model.RoomBooking;
import com.nivakaran.bookingservice.repository.RoomBookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomBookingService {

    private final RoomBookingRepository roomBookingRepository;
    private final HotelClient hotelClient;
    private final KafkaTemplate<String, BookingConfirmedEvent> kafkaTemplate;

    @Transactional
    public RoomBookingResponse createRoomBooking(RoomBookingRequest request, String idempotencyKey) {
        // 1. Check idempotency
        Optional<RoomBooking> existing = roomBookingRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return mapToResponse(existing.get());
        }

        // 2. Fetch actual room details and price
        RoomResponse room = hotelClient.getRoomById(request.roomId());
        if (room == null) {
            throw new ResourceNotFoundException("Room", request.roomId());
        }

        // 3. Validate availability with date range
        if (!hotelClient.isRoomAvailable(request.roomId())) {
            throw new BusinessRuleViolationException("Room is not available");
        }

        // 4. Check conflicting bookings
        List<RoomBooking> conflicts = roomBookingRepository.findConflictingBookings(
                request.roomId(), request.checkInDate(), request.checkOutDate());

        if (!conflicts.isEmpty()) {
            throw new BusinessRuleViolationException(
                    "Room is already booked for the selected dates");
        }

        // 5. Calculate using actual price
        long numberOfNights = ChronoUnit.DAYS.between(
                request.checkInDate(), request.checkOutDate());
        BigDecimal totalAmount = room.pricePerNight()
                .multiply(BigDecimal.valueOf(numberOfNights));

        // 6. Create booking
        RoomBooking booking = RoomBooking.builder()
                .bookingNumber(generateBookingNumber())
                .idempotencyKey(idempotencyKey)
                .roomId(request.roomId())
                .roomPrice(room.pricePerNight())
                .totalAmount(totalAmount)
                // ... other fields
                .status(BookingStatus.PENDING) // Start as PENDING
                .build();

        RoomBooking savedBooking = roomBookingRepository.save(booking);

        // 7. Reserve room (don't confirm yet)
        hotelClient.updateRoomStatus(request.roomId(), "RESERVED");

        // Don't send confirmation event yet - wait for payment

        return mapToResponse(savedBooking);
    }

    // New method for confirming booking after payment
    @Transactional
    public void confirmBooking(String bookingNumber) {
        RoomBooking booking = roomBookingRepository.findByBookingNumber(bookingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingNumber));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessRuleViolationException("Booking is not in pending state");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        roomBookingRepository.save(booking);

        // Now send confirmation event
        sendBookingConfirmationEvent(booking);
    }

    public List<RoomBookingResponse> getAllRoomBookings() {
        log.info("Fetching all room bookings");
        return roomBookingRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RoomBookingResponse getRoomBookingById(Long id) {
        log.info("Fetching room booking by id: {}", id);
        RoomBooking booking = roomBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room booking not found with id: " + id));
        return mapToResponse(booking);
    }

    public RoomBookingResponse getRoomBookingByNumber(String bookingNumber) {
        log.info("Fetching room booking by number: {}", bookingNumber);
        RoomBooking booking = roomBookingRepository.findByBookingNumber(bookingNumber)
                .orElseThrow(() -> new RuntimeException("Room booking not found with number: " + bookingNumber));
        return mapToResponse(booking);
    }

    public List<RoomBookingResponse> getRoomBookingsByGuestEmail(String email) {
        log.info("Fetching room bookings for guest: {}", email);
        return roomBookingRepository.findByGuestEmail(email).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RoomBookingResponse> getRoomBookingsByStatus(BookingStatus status) {
        log.info("Fetching room bookings by status: {}", status);
        return roomBookingRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomBookingResponse updateBookingStatus(Long id, BookingStatus status) {
        log.info("Updating room booking status for id: {} to: {}", id, status);

        RoomBooking booking = roomBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room booking not found with id: " + id));

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(status);
        booking.setLastModifiedDateTime(LocalDateTime.now());

        RoomBooking updatedBooking = roomBookingRepository.save(booking);

        // Update room status based on booking status
        try {
            switch (status) {
                case CHECKED_IN -> hotelClient.updateRoomStatus(booking.getRoomId(), "OCCUPIED");
                case CHECKED_OUT, COMPLETED -> hotelClient.updateRoomStatus(booking.getRoomId(), "AVAILABLE");
                case CANCELLED -> {
                    if (oldStatus == BookingStatus.CONFIRMED || oldStatus == BookingStatus.PENDING) {
                        hotelClient.updateRoomStatus(booking.getRoomId(), "AVAILABLE");
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to update room status: {}", e.getMessage());
        }

        log.info("Room booking status updated successfully");
        return mapToResponse(updatedBooking);
    }

    @Transactional
    public void cancelRoomBooking(Long id) {
        log.info("Cancelling room booking: {}", id);

        RoomBooking booking = roomBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room booking not found with id: " + id));

        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            throw new RuntimeException("Cannot cancel booking after check-in");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setLastModifiedDateTime(LocalDateTime.now());
        roomBookingRepository.save(booking);

        // Update room status to available
        try {
            hotelClient.updateRoomStatus(booking.getRoomId(), "AVAILABLE");
        } catch (Exception e) {
            log.error("Failed to update room status: {}", e.getMessage());
        }

        log.info("Room booking cancelled successfully");
    }

    private void sendBookingConfirmationEvent(RoomBooking booking) {
        BookingConfirmedEvent event = BookingConfirmedEvent.newBuilder()
                .setBookingNumber(booking.getBookingNumber())
                .setGuestEmail(booking.getGuestEmail())
                .setGuestName(booking.getGuestName())
                .setBookingType("ROOM")
                .setCheckInDate(booking.getCheckInDate().toString())
                .setCheckOutDate(booking.getCheckOutDate().toString())
                .setTotalAmount(booking.getTotalAmount().toString())
                .build();

        kafkaTemplate.send("booking-confirmed", event);
        log.info("Booking confirmation event sent for: {}", booking.getBookingNumber());
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "system";
    }

    private RoomBookingResponse mapToResponse(RoomBooking booking) {
        return new RoomBookingResponse(
                booking.getId(),
                booking.getBookingNumber(),
                booking.getRoomId(),
                booking.getGuestName(),
                booking.getGuestEmail(),
                booking.getGuestPhone(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getNumberOfGuests(),
                booking.getNumberOfNights(),
                booking.getRoomPrice(),
                booking.getTotalAmount(),
                booking.getStatus(),
                booking.getSpecialRequests(),
                booking.getBookingDateTime()
        );
    }
}