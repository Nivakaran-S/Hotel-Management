package com.nivakaran.restaurantservice.repository;

import com.nivakaran.restaurantservice.model.FoodCategory;
import com.nivakaran.restaurantservice.model.MenuItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MenuItemRepository extends MongoRepository<MenuItem, String> {
    List<MenuItem> findByCategory(FoodCategory category);
    List<MenuItem> findByIsAvailable(Boolean isAvailable);
    List<MenuItem> findByCategoryAndIsAvailable(FoodCategory category, Boolean isAvailable);
    List<MenuItem> findByIsVegetarian(Boolean isVegetarian);
    List<MenuItem> findByIsVegan(Boolean isVegan);
    List<MenuItem> findByNameContainingIgnoreCase(String name);

    // Optimized methods to combine filters at database level
    List<MenuItem> findByIsVegetarianAndIsAvailable(Boolean isVegetarian, Boolean isAvailable);
    List<MenuItem> findByIsVeganAndIsAvailable(Boolean isVegan, Boolean isAvailable);
    List<MenuItem> findByNameContainingIgnoreCaseAndIsAvailable(String keyword, Boolean isAvailable);
}