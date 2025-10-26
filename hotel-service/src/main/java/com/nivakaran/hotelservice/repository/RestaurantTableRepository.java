package com.nivakaran.hotelservice.repository;

import com.nivakaran.hotelservice.model.RestaurantTable;
import com.nivakaran.hotelservice.model.TableLocation;
import com.nivakaran.hotelservice.model.TableStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantTableRepository extends MongoRepository<RestaurantTable, String> {
    List<RestaurantTable> findByStatus(TableStatus status);
    List<RestaurantTable> findByLocation(TableLocation location);
    List<RestaurantTable> findByStatusAndCapacityGreaterThanEqual(TableStatus status, Integer capacity);
    Optional<RestaurantTable> findByTableNumber(String tableNumber);
}