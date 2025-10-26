package com.nivakaran.bookingservice.service;

import com.nivakaran.bookingservice.client.HotelClient;
import com.nivakaran.bookingservice.dto.TableBookingRequest;
import com.nivakaran.bookingservice.dto.TableBookingResponse;
import com.nivakaran.bookingservice.event.BookingConfirmedEvent;
import com.nivakaran.bookingservice.model.BookingStatus;
import com.nivakaran.bookingservice.model.TableBooking;
import com.nivakaran.bookingservice.repository.TableBookingRepository;
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
public class TableBookingService {

    private final TableBookingRepository tableBookingRepository;
    private final HotelClient hotelClient;
    private final KafkaTemplate<String, BookingConfirmedEvent> kafkaTemplate;

    @Transactional
    public TableBookingResponse createTableBooking(TableBookingRequest request) {
        log.info("Creating table booking for table: {}", request.tableId());

        // Check if table is available
        if (!hotelClient.isTableAvailable(request.tableId())) {
            throw new RuntimeException("Table is not available for booking");
        }

        // Check for conflicting bookings
        List<TableBooking> conflicts = tableBookingRepository.findConflictingBookings(
                request.tableId(), request.reservationDate(), request.reservationTime());

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Table is already booked for the selected time slot");
        }

        String bookingNumber = "TB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String bookedBy = getCurrentUsername();
        BigDecimal reservationFee = BigDecimal.valueOf(20); // This should come from hotel service

        TableBooking booking = TableBooking.builder()
                .bookingNumber(bookingNumber)
                .tableId(request.tableId())
                .guestName(request.guestName())
                .guestEmail(request.guestEmail())
                .guestPhone(request.guestPhone())
                .reservationDate(request.reservationDate())
                .reservationTime(request.reservationTime())
                .numberOfGuests(request.numberOfGuests())
                .durationMinutes(request.durationMinutes() != null ? request.durationMinutes() : 120)
                .reservationFee(reservationFee)
                .status(BookingStatus.CONFIRMED)
                .specialRequests(request.specialRequests())
                .bookingDateTime(LocalDateTime.now())
                .lastModifiedDateTime(LocalDateTime.now())
                .bookedBy(bookedBy)
                .build();

        TableBooking savedBooking = tableBookingRepository.save(booking);

        // Update table status to RESERVED
        try {
            hotelClient.updateTableStatus(request.tableId(), "RESERVED");
        } catch (Exception e) {
            log.error("Failed to update table status: {}", e.getMessage());
        }

        // Send booking confirmation event
        sendBookingConfirmationEvent(savedBooking);

        log.info("Table booking created successfully: {}", bookingNumber);
        return mapToResponse(savedBooking);
    }

    public List<TableBookingResponse> getAllTableBookings() {
        log.info("Fetching all table bookings");
        return tableBookingRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TableBookingResponse getTableBookingById(Long id) {
        log.info("Fetching table booking by id: {}", id);
        TableBooking booking = tableBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table booking not found with id: " + id));
        return mapToResponse(booking);
    }

    public TableBookingResponse getTableBookingByNumber(String bookingNumber) {
        log.info("Fetching table booking by number: {}", bookingNumber);
        TableBooking booking = tableBookingRepository.findByBookingNumber(bookingNumber)
                .orElseThrow(() -> new RuntimeException("Table booking not found with number: " + bookingNumber));
        return mapToResponse(booking);
    }

    public List<TableBookingResponse> getTableBookingsByGuestEmail(String email) {
        log.info("Fetching table bookings for guest: {}", email);
        return tableBookingRepository.findByGuestEmail(email).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TableBookingResponse> getTableBookingsByStatus(BookingStatus status) {
        log.info("Fetching table bookings by status: {}", status);
        return tableBookingRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TableBookingResponse updateBookingStatus(Long id, BookingStatus status) {
        log.info("Updating table booking status for id: {} to: {}", id, status);

        TableBooking booking = tableBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table booking not found with id: " + id));

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(status);
        booking.setLastModifiedDateTime(LocalDateTime.now());

        TableBooking updatedBooking = tableBookingRepository.save(booking);

        // Update table status based on booking status
        try {
            switch (status) {
                case CONFIRMED -> hotelClient.updateTableStatus(booking.getTableId(), "RESERVED");
                case COMPLETED -> hotelClient.updateTableStatus(booking.getTableId(), "AVAILABLE");
                case CANCELLED -> {
                    if (oldStatus == BookingStatus.CONFIRMED || oldStatus == BookingStatus.PENDING) {
                        hotelClient.updateTableStatus(booking.getTableId(), "AVAILABLE");
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to update table status: {}", e.getMessage());
        }

        log.info("Table booking status updated successfully");
        return mapToResponse(updatedBooking);
    }

    @Transactional
    public void cancelTableBooking(Long id) {
        log.info("Cancelling table booking: {}", id);

        TableBooking booking = tableBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table booking not found with id: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setLastModifiedDateTime(LocalDateTime.now());
        tableBookingRepository.save(booking);

        // Update table status to available
        try {
            hotelClient.updateTableStatus(booking.getTableId(), "AVAILABLE");
        } catch (Exception e) {
            log.error("Failed to update table status: {}", e.getMessage());
        }

        log.info("Table booking cancelled successfully");
    }

    private void sendBookingConfirmationEvent(TableBooking booking) {
        BookingConfirmedEvent event = BookingConfirmedEvent.newBuilder()
                .setBookingNumber(booking.getBookingNumber())
                .setGuestEmail(booking.getGuestEmail())
                .setGuestName(booking.getGuestName())
                .setBookingType("TABLE")
                .setCheckInDate(booking.getReservationDate().toString())
                .setCheckOutDate(booking.getReservationDate().toString())
                .setTotalAmount(booking.getReservationFee().toString())
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

    private TableBookingResponse mapToResponse(TableBooking booking) {
        return new TableBookingResponse(
                booking.getId(),
                booking.getBookingNumber(),
                booking.getTableId(),
                booking.getGuestName(),
                booking.getGuestEmail(),
                booking.getGuestPhone(),
                booking.getReservationDate(),
                booking.getReservationTime(),
                booking.getNumberOfGuests(),
                booking.getDurationMinutes(),
                booking.getReservationFee(),
                booking.getStatus(),
                booking.getSpecialRequests(),
                booking.getBookingDateTime()
        );
    }
}