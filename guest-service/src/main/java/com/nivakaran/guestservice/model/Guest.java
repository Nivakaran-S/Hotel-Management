package com.nivakaran.guestservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "guests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String guestId; // UUID

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    private LocalDate dateOfBirth;

    private String nationality;

    private String passportNumber;

    @Column(length = 500)
    private String address;

    private String city;

    private String state;

    private String country;

    private String zipCode;

    @Enumerated(EnumType.STRING)
    private GuestType guestType;

    @Column(length = 1000)
    private String preferences;

    @Column(length = 1000)
    private String specialRequests;

    private Boolean isActive;

    private Integer loyaltyPoints;

    @Enumerated(EnumType.STRING)
    private LoyaltyTier loyaltyTier;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    @Column(length = 1000)
    private String notes;
}