package com.nivakaran.staffservice.service;

import com.nivakaran.staffservice.dto.StaffRequest;
import com.nivakaran.staffservice.dto.StaffResponse;
import com.nivakaran.staffservice.model.Department;
import com.nivakaran.staffservice.model.EmploymentStatus;
import com.nivakaran.staffservice.model.StaffMember;
import com.nivakaran.staffservice.model.StaffRole;
import com.nivakaran.staffservice.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffService {

    private final StaffRepository staffRepository;

    @Transactional
    public StaffResponse createStaffMember(StaffRequest request) {
        log.info("Creating new staff member: {} {}", request.firstName(), request.lastName());

        // Check if email already exists
        if (staffRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Staff member with email " + request.email() + " already exists");
        }

        String staffId = "STF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String createdBy = getCurrentUsername();

        StaffMember staffMember = StaffMember.builder()
                .staffId(staffId)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .dateOfBirth(request.dateOfBirth())
                .address(request.address())
                .city(request.city())
                .state(request.state())
                .country(request.country())
                .zipCode(request.zipCode())
                .role(request.role())
                .department(request.department())
                .joinDate(request.joinDate())
                .employmentStatus(EmploymentStatus.ACTIVE)
                .salary(request.salary())
                .bankAccountNumber(request.bankAccountNumber())
                .emergencyContactName(request.emergencyContactName())
                .emergencyContactPhone(request.emergencyContactPhone())
                .qualifications(request.qualifications())
                .notes(request.notes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();

        StaffMember savedStaff = staffRepository.save(staffMember);
        log.info("Staff member created successfully with ID: {}", savedStaff.getStaffId());

        return mapToResponse(savedStaff);
    }

    public List<StaffResponse> getAllStaff() {
        log.info("Fetching all staff members");
        return staffRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public StaffResponse getStaffById(Long id) {
        log.info("Fetching staff member by id: {}", id);
        StaffMember staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff member not found with id: " + id));
        return mapToResponse(staff);
    }

    public StaffResponse getStaffByStaffId(String staffId) {
        log.info("Fetching staff member by staffId: {}", staffId);
        StaffMember staff = staffRepository.findByStaffId(staffId)
                .orElseThrow(() -> new RuntimeException("Staff member not found with staffId: " + staffId));
        return mapToResponse(staff);
    }

    public StaffResponse getStaffByEmail(String email) {
        log.info("Fetching staff member by email: {}", email);
        StaffMember staff = staffRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Staff member not found with email: " + email));
        return mapToResponse(staff);
    }

    public List<StaffResponse> getStaffByRole(StaffRole role) {
        log.info("Fetching staff members by role: {}", role);
        return staffRepository.findByRole(role).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<StaffResponse> getStaffByDepartment(Department department) {
        log.info("Fetching staff members by department: {}", department);
        return staffRepository.findByDepartment(department).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<StaffResponse> getActiveStaff() {
        log.info("Fetching active staff members");
        return staffRepository.findByEmploymentStatus(EmploymentStatus.ACTIVE).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<StaffResponse> searchStaff(String keyword) {
        log.info("Searching staff members with keyword: {}", keyword);
        return staffRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public StaffResponse updateStaffMember(Long id, StaffRequest request) {
        log.info("Updating staff member: {}", id);

        StaffMember staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff member not found with id: " + id));

        // Check email uniqueness if changed
        if (!staff.getEmail().equals(request.email())) {
            if (staffRepository.findByEmail(request.email()).isPresent()) {
                throw new RuntimeException("Email " + request.email() + " is already in use");
            }
        }

        staff.setFirstName(request.firstName());
        staff.setLastName(request.lastName());
        staff.setEmail(request.email());
        staff.setPhone(request.phone());
        staff.setDateOfBirth(request.dateOfBirth());
        staff.setAddress(request.address());
        staff.setCity(request.city());
        staff.setState(request.state());
        staff.setCountry(request.country());
        staff.setZipCode(request.zipCode());
        staff.setRole(request.role());
        staff.setDepartment(request.department());
        staff.setJoinDate(request.joinDate());
        staff.setSalary(request.salary());
        staff.setBankAccountNumber(request.bankAccountNumber());
        staff.setEmergencyContactName(request.emergencyContactName());
        staff.setEmergencyContactPhone(request.emergencyContactPhone());
        staff.setQualifications(request.qualifications());
        staff.setNotes(request.notes());
        staff.setUpdatedAt(LocalDateTime.now());

        StaffMember updatedStaff = staffRepository.save(staff);
        log.info("Staff member updated successfully: {}", updatedStaff.getStaffId());

        return mapToResponse(updatedStaff);
    }

    @Transactional
    public void updateEmploymentStatus(Long id, EmploymentStatus status) {
        log.info("Updating employment status for id: {} to: {}", id, status);

        StaffMember staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff member not found with id: " + id));

        staff.setEmploymentStatus(status);
        staff.setUpdatedAt(LocalDateTime.now());

        if (status == EmploymentStatus.RESIGNED || status == EmploymentStatus.TERMINATED) {
            staff.setResignationDate(java.time.LocalDate.now());
        }

        staffRepository.save(staff);
        log.info("Employment status updated successfully");
    }

    @Transactional
    public void deleteStaffMember(Long id) {
        log.info("Deleting staff member: {}", id);

        if (!staffRepository.existsById(id)) {
            throw new RuntimeException("Staff member not found with id: " + id);
        }

        staffRepository.deleteById(id);
        log.info("Staff member deleted successfully");
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "system";
    }

    private StaffResponse mapToResponse(StaffMember staff) {
        return new StaffResponse(
                staff.getId(),
                staff.getStaffId(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getEmail(),
                staff.getPhone(),
                staff.getDateOfBirth(),
                staff.getAddress(),
                staff.getCity(),
                staff.getState(),
                staff.getCountry(),
                staff.getZipCode(),
                staff.getRole(),
                staff.getDepartment(),
                staff.getJoinDate(),
                staff.getResignationDate(),
                staff.getEmploymentStatus(),
                staff.getSalary(),
                staff.getBankAccountNumber(),
                staff.getEmergencyContactName(),
                staff.getEmergencyContactPhone(),
                staff.getQualifications(),
                staff.getNotes(),
                staff.getCreatedAt()
        );
    }
}