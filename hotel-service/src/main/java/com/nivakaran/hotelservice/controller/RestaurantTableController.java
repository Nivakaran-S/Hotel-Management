package com.nivakaran.hotelservice.controller;

import com.nivakaran.hotelservice.dto.TableAvailabilityRequest;
import com.nivakaran.hotelservice.dto.TableRequest;
import com.nivakaran.hotelservice.dto.TableResponse;
import com.nivakaran.hotelservice.model.TableStatus;
import com.nivakaran.hotelservice.service.RestaurantTableService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotel/tables")
@RequiredArgsConstructor
@Validated
public class RestaurantTableController {

    private final RestaurantTableService tableService;

    @PostMapping
    @PreAuthorize("hasRole('admin') or hasRole('staff')")
    public ResponseEntity<TableResponse> createTable(@Valid @RequestBody TableRequest request) {
        TableResponse response = tableService.createTable(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TableResponse>> getAllTables() {
        List<TableResponse> tables = tableService.getAllTables();
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableResponse> getTableById(
            @PathVariable @NotBlank(message = "Table ID cannot be blank") String id) {
        TableResponse table = tableService.getTableById(id);
        return ResponseEntity.ok(table);
    }

    @GetMapping("/available")
    public ResponseEntity<List<TableResponse>> getAvailableTables() {
        List<TableResponse> tables = tableService.getAvailableTables();
        return ResponseEntity.ok(tables);
    }

    @PostMapping("/search")
    public ResponseEntity<List<TableResponse>> searchAvailableTables(
            @Valid @RequestBody TableAvailabilityRequest request) {
        List<TableResponse> tables = tableService.searchAvailableTables(request);
        return ResponseEntity.ok(tables);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('staff')")
    public ResponseEntity<TableResponse> updateTable(
            @PathVariable @NotBlank(message = "Table ID cannot be blank") String id,
            @Valid @RequestBody TableRequest request) {
        TableResponse response = tableService.updateTable(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('admin') or hasRole('staff')")
    public ResponseEntity<Void> updateTableStatus(
            @PathVariable @NotBlank(message = "Table ID cannot be blank") String id,
            @RequestParam TableStatus status) {
        tableService.updateTableStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteTable(
            @PathVariable @NotBlank(message = "Table ID cannot be blank") String id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> isTableAvailable(
            @PathVariable @NotBlank(message = "Table ID cannot be blank") String id) {
        boolean isAvailable = tableService.isTableAvailable(id);
        return ResponseEntity.ok(isAvailable);
    }
}
