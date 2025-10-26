package com.nivakaran.restaurantservice.service;

import com.nivakaran.restaurantservice.dto.MenuItemRequest;
import com.nivakaran.restaurantservice.dto.MenuItemResponse;
import com.nivakaran.restaurantservice.exception.MenuItemNotFoundException;
import com.nivakaran.restaurantservice.exception.MenuItemServiceException;
import com.nivakaran.restaurantservice.model.FoodCategory;
import com.nivakaran.restaurantservice.model.MenuItem;
import com.nivakaran.restaurantservice.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    @Transactional
    @CacheEvict(value = "menuItems", allEntries = true)
    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        log.info("Creating new menu item: {}", request.name());

        try {
            String createdBy = getCurrentUsername();

            MenuItem menuItem = MenuItem.builder()
                    .name(request.name())
                    .description(request.description())
                    .price(request.price())
                    .category(request.category())
                    .imageUrl(request.imageUrl())
                    .isAvailable(true)
                    .isVegetarian(Optional.ofNullable(request.isVegetarian()).orElse(false))
                    .isVegan(Optional.ofNullable(request.isVegan()).orElse(false))
                    .isGlutenFree(Optional.ofNullable(request.isGlutenFree()).orElse(false))
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
        } catch (Exception ex) {
            log.error("Error creating menu item: {}", ex.getMessage(), ex);
            throw new MenuItemServiceException("Failed to create menu item: " + ex.getMessage(), ex);
        }
    }

    @Cacheable("menuItems")
    public List<MenuItemResponse> getAllMenuItems() {
        log.info("Fetching all menu items");
        try {
            return menuItemRepository.findAll().stream()
                    .map(this::mapToResponse)
                    .toList();
        } catch (Exception ex) {
            log.error("Error fetching menu items: {}", ex.getMessage(), ex);
            throw new MenuItemServiceException("Failed to fetch menu items", ex);
        }
    }

    public MenuItemResponse getMenuItemById(String id) {
        log.info("Fetching menu item by id: {}", id);

        if (!StringUtils.hasText(id)) {
            throw new MenuItemServiceException("Menu item ID cannot be null or empty");
        }

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found with id: " + id));
        return mapToResponse(menuItem);
    }

    @Cacheable("availableMenuItems")
    public List<MenuItemResponse> getAvailableMenuItems() {
        log.info("Fetching available menu items");
        try {
            return menuItemRepository.findByIsAvailable(true).stream()
                    .map(this::mapToResponse)
                    .toList();
        } catch (Exception ex) {
            log.error("Error fetching available menu items: {}", ex.getMessage(), ex);
            throw new MenuItemServiceException("Failed to fetch available menu items", ex);
        }
    }

    public List<MenuItemResponse> getMenuItemsByCategory(FoodCategory category) {
        log.info("Fetching menu items by category: {}", category);

        if (category == null) {
            throw new MenuItemServiceException("Category cannot be null");
        }

        try {
            return menuItemRepository.findByCategoryAndIsAvailable(category, true).stream()
                    .map(this::mapToResponse)
                    .toList();
        } catch (Exception ex) {
            log.error("Error fetching menu items by category: {}", ex.getMessage(), ex);
            throw new MenuItemServiceException("Failed to fetch menu items by category", ex);
        }
    }

    public List<MenuItemResponse> getVegetarianMenuItems() {
        log.info("Fetching vegetarian menu items");
        try {
            // Using optimized repository method instead of filtering in service
            return menuItemRepository.findByIsVegetarianAndIsAvailable(true, true).stream()
                    .map(this::mapToResponse)
                    .toList();
        } catch (Exception ex) {
            log.error("Error fetching vegetarian menu items: {}", ex.getMessage(), ex);
            throw new MenuItemServiceException("Failed to fetch vegetarian menu items", ex);
        }
    }

    public List<MenuItemResponse> getVeganMenuItems() {
        log.info("Fetching vegan menu items");
        try {
            // Using optimized repository method instead of filtering in service
            return menuItemRepository.findByIsVeganAndIsAvailable(true, true).stream()
                    .map(this::mapToResponse)
                    .toList();
        } catch (Exception ex) {
            log.error("Error fetching vegan menu items: {}", ex.getMessage(), ex);
            throw new MenuItemServiceException("Failed to fetch vegan menu items", ex);
        }
    }

    public List<MenuItemResponse> searchMenuItems(String keyword) {
        log.info("Searching menu items with keyword: {}", keyword);

        if (!StringUtils.hasText(keyword)) {
            throw new MenuItemServiceException("Search keyword cannot be null or empty");
        }

        try {
            // Using optimized repository method instead of filtering in service
            return menuItemRepository.findByNameContainingIgnoreCaseAndIsAvailable(keyword, true).stream()
                    .map(this::mapToResponse)
                    .toList();
        } catch (Exception ex) {
            log.error("Error searching menu items: {}", ex.getMessage(), ex);
            throw new MenuItemServiceException("Failed to search menu items", ex);
        }
    }

    @Transactional
    @CacheEvict(value = {"menuItems", "availableMenuItems"}, allEntries = true)
    public MenuItemResponse updateMenuItem(String id, MenuItemRequest request) {
        log.info("Updating menu item: {}", id);

        if (!StringUtils.hasText(id)) {
            throw new MenuItemServiceException("Menu item ID cannot be null or empty");
        }

        try {
            MenuItem menuItem = menuItemRepository.findById(id)
                    .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found with id: " + id));

            // Update fields with null-safe handling
            menuItem.setName(request.name());
            menuItem.setDescription(request.description());
            menuItem.setPrice(request.price());
            menuItem.setCategory(request.category());
            menuItem.setImageUrl(request.imageUrl());
            menuItem.setIsVegetarian(Optional.ofNullable(request.isVegetarian()).orElse(menuItem.getIsVegetarian()));
            menuItem.setIsVegan(Optional.ofNullable(request.isVegan()).orElse(menuItem.getIsVegan()));
            menuItem.setIsGlutenFree(Optional.ofNullable(request.isGlutenFree()).orElse(menuItem.getIsGlutenFree()));
            menuItem.setIngredients(request.ingredients());
            menuItem.setPreparationTimeMinutes(request.preparationTimeMinutes());
            menuItem.setSpicyLevel(request.spicyLevel());
            menuItem.setUpdatedAt(LocalDateTime.now());

            MenuItem updatedMenuItem = menuItemRepository.save(menuItem);
            log.info("Menu item updated successfully: {}", updatedMenuItem.getName());

            return mapToResponse(updatedMenuItem);
        } catch (MenuItemNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating menu item: {}", ex.getMessage(), ex);
            throw new MenuItemServiceException("Failed to update menu item", ex);
        }
    }

    @Transactional
    @CacheEvict(value = {"menuItems", "availableMenuItems"}, allEntries = true)
    public void updateMenuItemAvailability(String id, Boolean isAvailable) {
        log.info("Updating menu item availability for id: {} to: {}", id, isAvailable);

        if (!StringUtils.hasText(id)) {
            throw new MenuItemServiceException("Menu item ID cannot be null or empty");
        }

        if (isAvailable == null) {
            throw new MenuItemServiceException("Availability status cannot be null");
        }

        try {
            MenuItem menuItem = menuItemRepository.findById(id)
                    .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found with id: " + id));

            menuItem.setIsAvailable(isAvailable);
            menuItem.setUpdatedAt(LocalDateTime.now());
            menuItemRepository.save(menuItem);

            log.info("Menu item availability updated successfully");
        } catch (MenuItemNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating menu item availability: {}", ex.getMessage(), ex);
            throw new MenuItemServiceException("Failed to update menu item availability", ex);
        }
    }

    @Transactional
    @CacheEvict(value = {"menuItems", "availableMenuItems"}, allEntries = true)
    public void deleteMenuItem(String id) {
        log.info("Deleting menu item: {}", id);

        if (!StringUtils.hasText(id)) {
            throw new MenuItemServiceException("Menu item ID cannot be null or empty");
        }

        try {
            if (!menuItemRepository.existsById(id)) {
                throw new MenuItemNotFoundException("Menu item not found with id: " + id);
            }

            menuItemRepository.deleteById(id);
            log.info("Menu item deleted successfully");
        } catch (MenuItemNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deleting menu item: {}", ex.getMessage(), ex);
            throw new MenuItemServiceException("Failed to delete menu item", ex);
        }
    }

    public boolean isMenuItemAvailable(String menuItemId) {
        if (!StringUtils.hasText(menuItemId)) {
            throw new MenuItemServiceException("Menu item ID cannot be null or empty");
        }

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found with id: " + menuItemId));
        return menuItem.getIsAvailable();
    }

    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
                return authentication.getName();
            }
        } catch (Exception ex) {
            log.warn("Error getting current username: {}", ex.getMessage());
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