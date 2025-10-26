package com.nivakaran.restaurantservice.dto;

import com.nivakaran.restaurantservice.model.FoodCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MenuItemResponse(
        String id,
        String name,
        String description,
        BigDecimal price,
        FoodCategory category,
        String imageUrl,
        Boolean isAvailable,
        Boolean isVegetarian,
        Boolean isVegan,
        Boolean isGlutenFree,
        String ingredients,
        Integer preparationTimeMinutes,
        String spicyLevel,
        LocalDateTime createdAt,
        String createdBy
) {}