package com.nivakaran.hotelservice.service;

import com.nivakaran.hotelservice.dto.TableAvailabilityRequest;
import com.nivakaran.hotelservice.dto.TableRequest;
import com.nivakaran.hotelservice.dto.TableResponse;
import com.nivakaran.hotelservice.model.RestaurantTable;
import com.nivakaran.hotelservice.model.TableStatus;
import com.nivakaran.hotelservice.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantTableService {

    private final RestaurantTableRepository tableRepository;

    @Transactional
    public TableResponse createTable(TableRequest request) {
        log.info("Creating new table: {}", request.tableNumber());

        if (tableRepository.findByTableNumber(request.tableNumber()).isPresent()) {
            throw new RuntimeException("Table number already exists: " + request.tableNumber());
        }

        RestaurantTable table = RestaurantTable.builder()
                .tableNumber(request.tableNumber())
                .capacity(request.capacity())
                .location(request.location())
                .status(TableStatus.AVAILABLE)
                .reservationFee(request.reservationFee())
                .isWindowSeat(request.isWindowSeat())
                .description(request.description())
                .build();

        RestaurantTable savedTable = tableRepository.save(table);
        log.info("Table created successfully: {}", savedTable.getTableNumber());

        return mapToResponse(savedTable);
    }

    public List<TableResponse> getAllTables() {
        log.info("Fetching all tables");
        return tableRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TableResponse getTableById(String id) {
        log.info("Fetching table by id: {}", id);
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));
        return mapToResponse(table);
    }

    public List<TableResponse> getAvailableTables() {
        log.info("Fetching available tables");
        return tableRepository.findByStatus(TableStatus.AVAILABLE).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TableResponse> searchAvailableTables(TableAvailabilityRequest request) {
        log.info("Searching available tables for capacity: {}", request.guestCount());

        return tableRepository.findByStatusAndCapacityGreaterThanEqual(
                        TableStatus.AVAILABLE, request.guestCount())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TableResponse updateTable(String id, TableRequest request) {
        log.info("Updating table: {}", id);

        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));

        table.setTableNumber(request.tableNumber());
        table.setCapacity(request.capacity());
        table.setLocation(request.location());
        table.setReservationFee(request.reservationFee());
        table.setIsWindowSeat(request.isWindowSeat());
        table.setDescription(request.description());

        RestaurantTable updatedTable = tableRepository.save(table);
        log.info("Table updated successfully: {}", updatedTable.getTableNumber());

        return mapToResponse(updatedTable);
    }

    @Transactional
    public void updateTableStatus(String id, TableStatus status) {
        log.info("Updating table status for id: {} to: {}", id, status);

        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));

        table.setStatus(status);
        tableRepository.save(table);

        log.info("Table status updated successfully");
    }

    @Transactional
    public void deleteTable(String id) {
        log.info("Deleting table: {}", id);

        if (!tableRepository.existsById(id)) {
            throw new RuntimeException("Table not found with id: " + id);
        }

        tableRepository.deleteById(id);
        log.info("Table deleted successfully");
    }

    public boolean isTableAvailable(String tableId) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + tableId));
        return table.getStatus() == TableStatus.AVAILABLE;
    }

    private TableResponse mapToResponse(RestaurantTable table) {
        return new TableResponse(
                table.getId(),
                table.getTableNumber(),
                table.getCapacity(),
                table.getLocation(),
                table.getStatus(),
                table.getReservationFee(),
                table.getIsWindowSeat(),
                table.getDescription()
        );
    }
}