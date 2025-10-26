package com.nivakaran.staffservice.dto;

import com.nivakaran.staffservice.model.Department;
import com.nivakaran.staffservice.model.StaffRole;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StaffRequest(
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

        String address,
        String city,
        String state,
        String country,
        String zipCode,

        @NotNull(message = "Role is required")
        StaffRole role,

        @NotNull(message = "Department is required")
        Department department,

        @NotNull(message = "Join date is required")
        LocalDate joinDate,

        @NotNull(message = "Salary is required")
        @Positive(message = "Salary must be positive")
        BigDecimal salary,

        String bankAccountNumber,
        String emergencyContactName,
        String emergencyContactPhone,
        String qualifications,
        String notes
) {}