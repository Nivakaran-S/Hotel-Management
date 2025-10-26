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
    public RoomBookingResponse createRoomBooking(RoomBookingRequest request) {
        log.info("Creating room booking for room: {}", request.roomId());

        // Validate dates
        if (request.checkOutDate().isBefore(request.checkInDate()) ||
                request.checkOutDate().isEqual(request.checkInDate())) {
            throw new RuntimeException("Check-out date must be after check-in date");
        }

        // Check if room is available
        if (!hotelClient.isRoomAvailable(request.roomId())) {
            throw new RuntimeException("Room is not available for booking");
        }

        // Check for conflicting bookings
        List<RoomBooking> conflicts = roomBookingRepository.findConflictingBookings(
                request.roomId(), request.checkInDate(), request.checkOutDate());

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Room is already booked for the selected dates");
        }

        // Calculate number of nights and total amount
        long numberOfNights = ChronoUnit.DAYS.between(request.checkInDate(), request.checkOutDate());
        BigDecimal roomPricePerNight = BigDecimal.valueOf(100); // This should come from hotel service
        BigDecimal totalAmount = roomPricePerNight.multiply(BigDecimal.valueOf(numberOfNights));

        String bookingNumber = "RB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String bookedBy = getCurrentUsername();

        RoomBooking booking = RoomBooking.builder()
                .bookingNumber(bookingNumber)
                .roomId(request.roomId())
                .guestName(request.guestName())
                .guestEmail(request.guestEmail())
                .guestPhone(request.guestPhone())
                .checkInDate(request.checkInDate())
                .checkOutDate(request.checkOutDate())
                .numberOfGuests(request.numberOfGuests())
                .numberOfNights((int) numberOfNights)
                .roomPrice(roomPricePerNight)
                .totalAmount(totalAmount)
                .status(BookingStatus.CONFIRMED)
                .specialRequests(request.specialRequests())
                .bookingDateTime(LocalDateTime.now())
                .lastModifiedDateTime(LocalDateTime.now())
                .bookedBy(bookedBy)
                .build();

        RoomBooking savedBooking = roomBookingRepository.save(booking);

        // Update room status to RESERVED
        try {
            hotelClient.updateRoomStatus(request.roomId(), "RESERVED");
        } catch (Exception e) {
            log.error("Failed to update room status: {}", e.getMessage());
        }

        // Send booking confirmation event
        sendBookingConfirmationEvent(savedBooking);

        log.info("Room booking created successfully: {}", bookingNumber);
        return mapToResponse(savedBooking);
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