package com.nivakaran.guestservice.dto;

import com.nivakaran.guestservice.model.GuestType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GuestRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Phone is required")
        String phone,

        LocalDate dateOfBirth,

        String nationality,

        String passportNumber,

        String address,

        String city,

        String state,

        String country,

        String zipCode,

        @NotNull(message = "Guest type is required")
        GuestType guestType,

        String preferences,

        String specialRequests,

        String notes
) {}