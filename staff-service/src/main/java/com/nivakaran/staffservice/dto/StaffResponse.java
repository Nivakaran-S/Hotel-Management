package com.nivakaran.staffservice.dto;

import com.nivakaran.staffservice.model.Department;
import com.nivakaran.staffservice.model.EmploymentStatus;
import com.nivakaran.staffservice.model.StaffRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StaffResponse(
        Long id,
        String staffId,
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate dateOfBirth,
        String address,
        String city,
        String state,
        String country,
        String zipCode,
        StaffRole role,
        Department department,
        LocalDate joinDate,
        LocalDate resignationDate,
        EmploymentStatus employmentStatus,
        BigDecimal salary,
        String bankAccountNumber,
        String emergencyContactName,
        String emergencyContactPhone,
        String qualifications,
        String notes,
        LocalDateTime createdAt
) {}