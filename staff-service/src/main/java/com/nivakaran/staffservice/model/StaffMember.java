package com.nivakaran.staffservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff_members")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaffMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String staffId; // UUID

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    private LocalDate dateOfBirth;

    @Column(length = 500)
    private String address;

    private String city;
    private String state;
    private String country;
    private String zipCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @Column(nullable = false)
    private LocalDate joinDate;

    private LocalDate resignationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentStatus employmentStatus;

    @Column(nullable = false)
    private BigDecimal salary;

    private String bankAccountNumber;
    private String emergencyContactName;
    private String emergencyContactPhone;

    @Column(length = 1000)
    private String qualifications;

    @Column(length = 1000)
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}