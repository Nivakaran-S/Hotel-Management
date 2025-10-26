package com.nivakaran.bookingservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "table_bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookingNumber;
    private String tableId;
    private String guestId;
    private String guestName;
    private String guestEmail;
    private String guestPhone;

    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private Integer numberOfGuests;
    private Integer durationMinutes;

    private BigDecimal reservationFee;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private String specialRequests;
    private LocalDateTime bookingDateTime;
    private LocalDateTime lastModifiedDateTime;
    private String bookedBy;

    // Builder pattern manually
    public static TableBookingBuilder builder() {
        return new TableBookingBuilder();
    }

    public static class TableBookingBuilder {
        private TableBooking booking = new TableBooking();

        public TableBookingBuilder bookingNumber(String bookingNumber) {
            booking.bookingNumber = bookingNumber;
            return this;
        }

        public TableBookingBuilder tableId(String tableId) {
            booking.tableId = tableId;
            return this;
        }

        public TableBookingBuilder guestName(String guestName) {
            booking.guestName = guestName;
            return this;
        }

        public TableBookingBuilder guestEmail(String guestEmail) {
            booking.guestEmail = guestEmail;
            return this;
        }

        public TableBookingBuilder guestPhone(String guestPhone) {
            booking.guestPhone = guestPhone;
            return this;
        }

        public TableBookingBuilder reservationDate(LocalDate reservationDate) {
            booking.reservationDate = reservationDate;
            return this;
        }

        public TableBookingBuilder reservationTime(LocalTime reservationTime) {
            booking.reservationTime = reservationTime;
            return this;
        }

        public TableBookingBuilder numberOfGuests(Integer numberOfGuests) {
            booking.numberOfGuests = numberOfGuests;
            return this;
        }

        public TableBookingBuilder durationMinutes(Integer durationMinutes) {
            booking.durationMinutes = durationMinutes;
            return this;
        }

        public TableBookingBuilder reservationFee(BigDecimal reservationFee) {
            booking.reservationFee = reservationFee;
            return this;
        }

        public TableBookingBuilder status(BookingStatus status) {
            booking.status = status;
            return this;
        }

        public TableBookingBuilder specialRequests(String specialRequests) {
            booking.specialRequests = specialRequests;
            return this;
        }

        public TableBookingBuilder bookingDateTime(LocalDateTime bookingDateTime) {
            booking.bookingDateTime = bookingDateTime;
            return this;
        }

        public TableBookingBuilder lastModifiedDateTime(LocalDateTime lastModifiedDateTime) {
            booking.lastModifiedDateTime = lastModifiedDateTime;
            return this;
        }

        public TableBookingBuilder bookedBy(String bookedBy) {
            booking.bookedBy = bookedBy;
            return this;
        }

        public TableBooking build() {
            return booking;
        }
    }
}