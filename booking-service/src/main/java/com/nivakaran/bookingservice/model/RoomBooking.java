package com.nivakaran.bookingservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookingNumber;
    private String roomId;
    private String guestId;
    private String guestName;
    private String guestEmail;
    private String guestPhone;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private Integer numberOfNights;

    private BigDecimal roomPrice;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private String specialRequests;
    private LocalDateTime bookingDateTime;
    private LocalDateTime lastModifiedDateTime;
    private String bookedBy;

    @Column(unique = true)
    private String idempotencyKey;

    // Builder pattern manually
    public static RoomBookingBuilder builder() {
        return new RoomBookingBuilder();
    }

    public static class RoomBookingBuilder {
        private RoomBooking booking = new RoomBooking();

        public RoomBookingBuilder bookingNumber(String bookingNumber) {
            booking.bookingNumber = bookingNumber;
            return this;
        }

        public RoomBookingBuilder idempotencyKey(String idempotencyKey) {
            booking.idempotencyKey = idempotencyKey;
            return this;
        }

        public RoomBookingBuilder roomId(String roomId) {
            booking.roomId = roomId;
            return this;
        }

        public RoomBookingBuilder guestName(String guestName) {
            booking.guestName = guestName;
            return this;
        }

        public RoomBookingBuilder guestEmail(String guestEmail) {
            booking.guestEmail = guestEmail;
            return this;
        }

        public RoomBookingBuilder guestPhone(String guestPhone) {
            booking.guestPhone = guestPhone;
            return this;
        }

        public RoomBookingBuilder checkInDate(LocalDate checkInDate) {
            booking.checkInDate = checkInDate;
            return this;
        }

        public RoomBookingBuilder checkOutDate(LocalDate checkOutDate) {
            booking.checkOutDate = checkOutDate;
            return this;
        }

        public RoomBookingBuilder numberOfGuests(Integer numberOfGuests) {
            booking.numberOfGuests = numberOfGuests;
            return this;
        }

        public RoomBookingBuilder numberOfNights(Integer numberOfNights) {
            booking.numberOfNights = numberOfNights;
            return this;
        }

        public RoomBookingBuilder roomPrice(BigDecimal roomPrice) {
            booking.roomPrice = roomPrice;
            return this;
        }

        public RoomBookingBuilder totalAmount(BigDecimal totalAmount) {
            booking.totalAmount = totalAmount;
            return this;
        }

        public RoomBookingBuilder status(BookingStatus status) {
            booking.status = status;
            return this;
        }

        public RoomBookingBuilder specialRequests(String specialRequests) {
            booking.specialRequests = specialRequests;
            return this;
        }

        public RoomBookingBuilder bookingDateTime(LocalDateTime bookingDateTime) {
            booking.bookingDateTime = bookingDateTime;
            return this;
        }

        public RoomBookingBuilder lastModifiedDateTime(LocalDateTime lastModifiedDateTime) {
            booking.lastModifiedDateTime = lastModifiedDateTime;
            return this;
        }

        public RoomBookingBuilder bookedBy(String bookedBy) {
            booking.bookedBy = bookedBy;
            return this;
        }

        public RoomBooking build() {
            return booking;
        }
    }
}