package com.nivakaran.staffservice.controller;

import com.nivakaran.staffservice.dto.StaffRequest;
import com.nivakaran.staffservice.dto.StaffResponse;
import com.nivakaran.staffservice.model.Department;
import com.nivakaran.staffservice.model.EmploymentStatus;
import com.nivakaran.staffservice.model.StaffRole;
import com.nivakaran.staffservice.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('admin') or hasRole('hr_manager')")
    public StaffResponse createStaffMember(@Valid @RequestBody StaffRequest request) {
        return staffService.createStaffMember(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('admin') or hasRole('hr_manager') or hasRole('manager')")
    public List<StaffResponse> getAllStaff() {
        return staffService.getAllStaff();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('hr_manager') or hasRole('manager')")
    public StaffResponse getStaffById(@PathVariable Long id) {
        return staffService.getStaffById(id);
    }

    @GetMapping("/staff-id/{staffId}")
    @PreAuthorize("hasRole('admin') or hasRole('hr_manager') or hasRole('manager')")
    public StaffResponse getStaffByStaffId(@PathVariable String staffId) {
        return staffService.getStaffByStaffId(staffId);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('admin') or hasRole('hr_manager') or hasRole('manager')")
    public StaffResponse getStaffByEmail(@PathVariable String email) {
        return staffService.getStaffByEmail(email);
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('admin') or hasRole('hr_manager') or hasRole('manager')")
    public List<StaffResponse> getStaffByRole(@PathVariable StaffRole role) {
        return staffService.getStaffByRole(role);
    }

    @GetMapping("/department/{department}")
    @PreAuthorize("hasRole('admin') or hasRole('hr_manager') or hasRole('manager')")
    public List<StaffResponse> getStaffByDepartment(@PathVariable Department department) {
        return staffService.getStaffByDepartment(department);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('admin') or hasRole('hr_manager') or hasRole('manager')")
    public List<StaffResponse> getActiveStaff() {
        return staffService.getActiveStaff();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('admin') or hasRole('hr_manager') or hasRole('manager')")
    public List<StaffResponse> searchStaff(@RequestParam String keyword) {
        return staffService.searchStaff(keyword);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('hr_manager')")
    public StaffResponse updateStaffMember(@PathVariable Long id, @Valid @RequestBody StaffRequest request) {
        return staffService.updateStaffMember(id, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('admin') or hasRole('hr_manager')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEmploymentStatus(@PathVariable Long id, @RequestParam EmploymentStatus status) {
        staffService.updateEmploymentStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStaffMember(@PathVariable Long id) {
        staffService.deleteStaffMember(id);
    }
}