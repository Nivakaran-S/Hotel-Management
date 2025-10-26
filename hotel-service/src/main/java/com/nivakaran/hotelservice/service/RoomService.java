package com.nivakaran.hotelservice.service;

import com.nivakaran.hotelservice.dto.RoomAvailabilityRequest;
import com.nivakaran.hotelservice.dto.RoomRequest;
import com.nivakaran.hotelservice.dto.RoomResponse;
import com.nivakaran.hotelservice.model.Room;
import com.nivakaran.hotelservice.model.RoomStatus;
import com.nivakaran.hotelservice.model.RoomType;
import com.nivakaran.hotelservice.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        log.info("Creating new room: {}", request.roomNumber());

        // Check if room number already exists
        if (roomRepository.findByRoomNumber(request.roomNumber()).isPresent()) {
            throw new RuntimeException("Room number already exists: " + request.roomNumber());
        }

        Room room = Room.builder()
                .roomNumber(request.roomNumber())
                .roomType(request.roomType())
                .pricePerNight(request.pricePerNight())
                .capacity(request.capacity())
                .status(RoomStatus.AVAILABLE)
                .description(request.description())
                .floor(request.floor())
                .hasBalcony(request.hasBalcony())
                .hasSeaView(request.hasSeaView())
                .amenities(request.amenities())
                .build();

        Room savedRoom = roomRepository.save(room);
        log.info("Room created successfully: {}", savedRoom.getRoomNumber());

        return mapToResponse(savedRoom);
    }

    public List<RoomResponse> getAllRooms() {
        log.info("Fetching all rooms");
        return roomRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RoomResponse getRoomById(String id) {
        log.info("Fetching room by id: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
        return mapToResponse(room);
    }

    public List<RoomResponse> getAvailableRooms() {
        log.info("Fetching available rooms");
        return roomRepository.findByStatus(RoomStatus.AVAILABLE).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RoomResponse> getRoomsByType(RoomType roomType) {
        log.info("Fetching rooms by type: {}", roomType);
        return roomRepository.findByRoomType(roomType).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RoomResponse> searchAvailableRooms(RoomAvailabilityRequest request) {
        log.info("Searching available rooms for type: {}, capacity: {}",
                request.roomType(), request.guestCount());

        return roomRepository.findByRoomTypeAndStatusAndCapacityGreaterThanEqual(
                        request.roomType(), RoomStatus.AVAILABLE, request.guestCount())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomResponse updateRoom(String id, RoomRequest request) {
        log.info("Updating room: {}", id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));

        room.setRoomNumber(request.roomNumber());
        room.setRoomType(request.roomType());
        room.setPricePerNight(request.pricePerNight());
        room.setCapacity(request.capacity());
        room.setDescription(request.description());
        room.setFloor(request.floor());
        room.setHasBalcony(request.hasBalcony());
        room.setHasSeaView(request.hasSeaView());
        room.setAmenities(request.amenities());

        Room updatedRoom = roomRepository.save(room);
        log.info("Room updated successfully: {}", updatedRoom.getRoomNumber());

        return mapToResponse(updatedRoom);
    }

    @Transactional
    public void updateRoomStatus(String id, RoomStatus status) {
        log.info("Updating room status for id: {} to: {}", id, status);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));

        room.setStatus(status);
        roomRepository.save(room);

        log.info("Room status updated successfully");
    }

    @Transactional
    public void deleteRoom(String id) {
        log.info("Deleting room: {}", id);

        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room not found with id: " + id);
        }

        roomRepository.deleteById(id);
        log.info("Room deleted successfully");
    }

    public boolean isRoomAvailable(String roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
        return room.getStatus() == RoomStatus.AVAILABLE;
    }

    private RoomResponse mapToResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getPricePerNight(),
                room.getCapacity(),
                room.getStatus(),
                room.getDescription(),
                room.getFloor(),
                room.getHasBalcony(),
                room.getHasSeaView(),
                room.getAmenities()
        );
    }
}