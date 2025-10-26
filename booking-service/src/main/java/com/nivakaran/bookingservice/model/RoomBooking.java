package com.nivakaran.bookingservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_bookings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
}