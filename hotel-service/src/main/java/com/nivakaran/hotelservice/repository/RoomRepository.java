package com.nivakaran.hotelservice.repository;

import com.nivakaran.hotelservice.model.Room;
import com.nivakaran.hotelservice.model.RoomStatus;
import com.nivakaran.hotelservice.model.RoomType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String> {
    List<Room> findByRoomType(RoomType roomType);
    List<Room> findByStatus(RoomStatus status);
    List<Room> findByRoomTypeAndStatus(RoomType roomType, RoomStatus status);
    Optional<Room> findByRoomNumber(String roomNumber);
    List<Room> findByRoomTypeAndStatusAndCapacityGreaterThanEqual(
            RoomType roomType, RoomStatus status, Integer capacity);
}