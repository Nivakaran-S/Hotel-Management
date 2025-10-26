package com.nivakaran.restuarantservice.service;

import com.nivakaran.restaurantservice.dto.MenuItemRequest;
import com.nivakaran.restaurantservice.dto.MenuItemResponse;
import com.nivakaran.restaurantservice.model.FoodCategory;
import com.nivakaran.restaurantservice.model.MenuItem;
import com.nivakaran.restaurantservice.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    @Transactional
    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        log.info("Creating new menu item: {}", request.name());

        String createdBy = getCurrentUsername();

        MenuItem menuItem = MenuItem.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(request.category())
                .imageUrl(request.imageUrl())
                .isAvailable(true)
                .isVegetarian(request.isVegetarian() != null ? request.isVegetarian() : false)
                .isVegan(request.isVegan() != null ? request.isVegan() : false)
                .isGlutenFree(request.isGlutenFree() != null ? request.isGlutenFree() : false)
                .ingredients(request.ingredients())
                .preparationTimeMinutes(request.preparationTimeMinutes())
                .spicyLevel(request.spicyLevel())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        log.info("Menu item created successfully: {}", savedMenuItem.getName());

        return mapToResponse(savedMenuItem);
    }

    public List<MenuItemResponse> getAllMenuItems() {
        log.info("Fetching all menu items");
        return menuItemRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public MenuItemResponse getMenuItemById(String id) {
        log.info("Fetching menu item by id: {}", id);
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
        return mapToResponse(menuItem);
    }

    public List<MenuItemResponse> getAvailableMenuItems() {
        log.info("Fetching available menu items");
        return menuItemRepository.findByIsAvailable(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MenuItemResponse> getMenuItemsByCategory(FoodCategory category) {
        log.info("Fetching menu items by category: {}", category);
        return menuItemRepository.findByCategoryAndIsAvailable(category, true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MenuItemResponse> getVegetarianMenuItems() {
        log.info("Fetching vegetarian menu items");
        return menuItemRepository.findByIsVegetarian(true).stream()
                .filter(item -> item.getIsAvailable())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MenuItemResponse> getVeganMenuItems() {
        log.info("Fetching vegan menu items");
        return menuItemRepository.findByIsVegan(true).stream()
                .filter(item -> item.getIsAvailable())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MenuItemResponse> searchMenuItems(String keyword) {
        log.info("Searching menu items with keyword: {}", keyword);
        return menuItemRepository.findByNameContainingIgnoreCase(keyword).stream()
                .filter(item -> item.getIsAvailable())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MenuItemResponse updateMenuItem(String id, MenuItemRequest request) {
        log.info("Updating menu item: {}", id);

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));

        menuItem.setName(request.name());
        menuItem.setDescription(request.description());
        menuItem.setPrice(request.price());
        menuItem.setCategory(request.category());
        menuItem.setImageUrl(request.imageUrl());
        menuItem.setIsVegetarian(request.isVegetarian());
        menuItem.setIsVegan(request.isVegan());
        menuItem.setIsGlutenFree(request.isGlutenFree());
        menuItem.setIngredients(request.ingredients());
        menuItem.setPreparationTimeMinutes(request.preparationTimeMinutes());
        menuItem.setSpicyLevel(request.spicyLevel());
        menuItem.setUpdatedAt(LocalDateTime.now());

        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);
        log.info("Menu item updated successfully: {}", updatedMenuItem.getName());

        return mapToResponse(updatedMenuItem);
    }

    @Transactional
    public void updateMenuItemAvailability(String id, Boolean isAvailable) {
        log.info("Updating menu item availability for id: {} to: {}", id, isAvailable);

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));

        menuItem.setIsAvailable(isAvailable);
        menuItem.setUpdatedAt(LocalDateTime.now());
        menuItemRepository.save(menuItem);

        log.info("Menu item availability updated successfully");
    }

    @Transactional
    public void deleteMenuItem(String id) {
        log.info("Deleting menu item: {}", id);

        if (!menuItemRepository.existsById(id)) {
            throw new RuntimeException("Menu item not found with id: " + id);
        }

        menuItemRepository.deleteById(id);
        log.info("Menu item deleted successfully");
    }

    public boolean isMenuItemAvailable(String menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + menuItemId));
        return menuItem.getIsAvailable();
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "system";
    }

    private MenuItemResponse mapToResponse(MenuItem menuItem) {
        return new MenuItemResponse(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getCategory(),
                menuItem.getImageUrl(),
                menuItem.getIsAvailable(),
                menuItem.getIsVegetarian(),
                menuItem.getIsVegan(),
                menuItem.getIsGlutenFree(),
                menuItem.getIngredients(),
                menuItem.getPreparationTimeMinutes(),
                menuItem.getSpicyLevel(),
                menuItem.getCreatedAt(),
                menuItem.getCreatedBy()
        );
    }
}