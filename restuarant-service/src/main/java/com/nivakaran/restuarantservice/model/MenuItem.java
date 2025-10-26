package com.nivakaran.restuarantservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "menu_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    @Id
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private FoodCategory category;
    private String imageUrl;
    private Boolean isAvailable;
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isGlutenFree;
    private String ingredients;
    private Integer preparationTimeMinutes;
    private String spicyLevel; // MILD, MEDIUM, HOT
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}