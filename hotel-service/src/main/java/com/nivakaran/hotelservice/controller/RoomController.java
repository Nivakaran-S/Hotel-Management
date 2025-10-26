package com.nivakaran.hotelservice.controller;

import com.nivakaran.hotelservice.dto.RoomAvailabilityRequest;
import com.nivakaran.hotelservice.dto.RoomRequest;
import com.nivakaran.hotelservice.dto.RoomResponse;
import com.nivakaran.hotelservice.model.RoomStatus;
import com.nivakaran.hotelservice.model.RoomType;
import com.nivakaran.hotelservice.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotel/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('admin') or hasRole('staff')")
    public RoomResponse createRoom(@Valid @RequestBody RoomRequest request) {
        return roomService.createRoom(request);
    }

    @GetMapping
    public List<RoomResponse> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    public RoomResponse getRoomById(@PathVariable String id) {
        return roomService.getRoomById(id);
    }

    @GetMapping("/available")
    public List<RoomResponse> getAvailableRooms() {
        return roomService.getAvailableRooms();
    }

    @GetMapping("/type/{roomType}")
    public List<RoomResponse> getRoomsByType(@PathVariable RoomType roomType) {
        return roomService.getRoomsByType(roomType);
    }

    @PostMapping("/search")
    public List<RoomResponse> searchAvailableRooms(@RequestBody RoomAvailabilityRequest request) {
        return roomService.searchAvailableRooms(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('staff')")
    public RoomResponse updateRoom(@PathVariable String id, @Valid @RequestBody RoomRequest request) {
        return roomService.updateRoom(id, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('admin') or hasRole('staff')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRoomStatus(@PathVariable String id, @RequestParam RoomStatus status) {
        roomService.updateRoomStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable String id) {
        roomService.deleteRoom(id);
    }

    @GetMapping("/{id}/availability")
    public boolean isRoomAvailable(@PathVariable String id) {
        return roomService.isRoomAvailable(id);
    }
}