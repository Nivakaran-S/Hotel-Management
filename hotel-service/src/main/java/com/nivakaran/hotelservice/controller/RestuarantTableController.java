package com.nivakaran.hotelservice.controller;

import com.nivakaran.hotelservice.dto.TableAvailabilityRequest;
import com.nivakaran.hotelservice.dto.TableRequest;
import com.nivakaran.hotelservice.dto.TableResponse;
import com.nivakaran.hotelservice.model.TableStatus;
import com.nivakaran.hotelservice.service.RestaurantTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotel/tables")
@RequiredArgsConstructor
public class RestaurantTableController {

    private final RestaurantTableService tableService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('admin') or hasRole('staff')")
    public TableResponse createTable(@Valid @RequestBody TableRequest request) {
        return tableService.createTable(request);
    }

    @GetMapping
    public List<TableResponse> getAllTables() {
        return tableService.getAllTables();
    }

    @GetMapping("/{id}")
    public TableResponse getTableById(@PathVariable String id) {
        return tableService.getTableById(id);
    }

    @GetMapping("/available")
    public List<TableResponse> getAvailableTables() {
        return tableService.getAvailableTables();
    }

    @PostMapping("/search")
    public List<TableResponse> searchAvailableTables(@RequestBody TableAvailabilityRequest request) {
        return tableService.searchAvailableTables(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('staff')")
    public TableResponse updateTable(@PathVariable String id, @Valid @RequestBody TableRequest request) {
        return tableService.updateTable(id, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('admin') or hasRole('staff')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTableStatus(@PathVariable String id, @RequestParam TableStatus status) {
        tableService.updateTableStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTable(@PathVariable String id) {
        tableService.deleteTable(id);
    }

    @GetMapping("/{id}/availability")
    public boolean isTableAvailable(@PathVariable String id) {
        return tableService.isTableAvailable(id);
    }
}