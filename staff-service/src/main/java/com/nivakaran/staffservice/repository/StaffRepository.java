package com.nivakaran.staffservice.repository;

import com.nivakaran.staffservice.model.Department;
import com.nivakaran.staffservice.model.EmploymentStatus;
import com.nivakaran.staffservice.model.StaffMember;
import com.nivakaran.staffservice.model.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<StaffMember, Long> {
    Optional<StaffMember> findByStaffId(String staffId);
    Optional<StaffMember> findByEmail(String email);
    List<StaffMember> findByRole(StaffRole role);
    List<StaffMember> findByDepartment(Department department);
    List<StaffMember> findByEmploymentStatus(EmploymentStatus employmentStatus);
    List<StaffMember> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}