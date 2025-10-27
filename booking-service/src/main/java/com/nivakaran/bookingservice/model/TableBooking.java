package com.nivakaran.bookingservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "table_bookings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
}