package com.nivakaran.restaurantservice.dto;

import com.nivakaran.restaurantservice.model.FoodCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MenuItemRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotNull(message = "Category is required")
        FoodCategory category,

        String imageUrl,
        Boolean isVegetarian,
        Boolean isVegan,
        Boolean isGlutenFree,
        String ingredients,
        Integer preparationTimeMinutes,
        String spicyLevel
) {}