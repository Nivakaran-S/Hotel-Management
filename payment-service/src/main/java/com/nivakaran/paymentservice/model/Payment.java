package com.nivakaran.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String paymentId; // UUID

    @Column(nullable = false)
    private String bookingNumber; // Reference to booking

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType; // ROOM_BOOKING, TABLE_BOOKING, FOOD_ORDER

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    private String transactionId; // External payment gateway transaction ID

    private String guestEmail;

    private String guestName;

    @Column(length = 1000)
    private String description;

    private LocalDateTime paymentDateTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String processedBy;

    @Column(length = 500)
    private String failureReason;

    private String cardLast4Digits; // For card payments

    private String cardType; // VISA, MASTERCARD, etc.
}