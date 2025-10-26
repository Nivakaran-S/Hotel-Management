package com.nivakaran.guestservice.dto;

import com.nivakaran.guestservice.model.GuestType;
import com.nivakaran.guestservice.model.LoyaltyTier;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record GuestResponse(
        Long id,
        String guestId,
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate dateOfBirth,
        String nationality,
        String passportNumber,
        String address,
        String city,
        String state,
        String country,
        String zipCode,
        GuestType guestType,
        String preferences,
        String specialRequests,
        Boolean isActive,
        Integer loyaltyPoints,
        LoyaltyTier loyaltyTier,
        LocalDateTime createdAt,
        String notes
) {}