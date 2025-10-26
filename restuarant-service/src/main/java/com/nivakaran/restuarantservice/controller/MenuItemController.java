package com.nivakaran.restuarantservice.controller;

import com.nivakaran.restaurantservice.dto.MenuItemRequest;
import com.nivakaran.restaurantservice.dto.MenuItemResponse;
import com.nivakaran.restaurantservice.model.FoodCategory;
import com.nivakaran.restaurantservice.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant/menu")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('admin') or hasRole('staff')")
    public MenuItemResponse createMenuItem(@Valid @RequestBody MenuItemRequest request) {
        return menuItemService.createMenuItem(request);
    }

    @GetMapping
    public List<MenuItemResponse> getAllMenuItems() {
        return menuItemService.getAllMenuItems();
    }

    @GetMapping("/{id}")
    public MenuItemResponse getMenuItemById(@PathVariable String id) {
        return menuItemService.getMenuItemById(id);
    }

    @GetMapping("/available")
    public List<MenuItemResponse> getAvailableMenuItems() {
        return menuItemService.getAvailableMenuItems();
    }

    @GetMapping("/category/{category}")
    public List<MenuItemResponse> getMenuItemsByCategory(@PathVariable FoodCategory category) {
        return menuItemService.getMenuItemsByCategory(category);
    }

    @GetMapping("/vegetarian")
    public List<MenuItemResponse> getVegetarianMenuItems() {
        return menuItemService.getVegetarianMenuItems();
    }

    @GetMapping("/vegan")
    public List<MenuItemResponse> getVeganMenuItems() {
        return menuItemService.getVeganMenuItems();
    }

    @GetMapping("/search")
    public List<MenuItemResponse> searchMenuItems(@RequestParam String keyword) {
        return menuItemService.searchMenuItems(keyword);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('staff')")
    public MenuItemResponse updateMenuItem(@PathVariable String id, @Valid @RequestBody MenuItemRequest request) {
        return menuItemService.updateMenuItem(id, request);
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('admin') or hasRole('staff')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMenuItemAvailability(@PathVariable String id, @RequestParam Boolean isAvailable) {
        menuItemService.updateMenuItemAvailability(id, isAvailable);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@PathVariable String id) {
        menuItemService.deleteMenuItem(id);
    }

    @GetMapping("/{id}/available")
    public boolean isMenuItemAvailable(@PathVariable String id) {
        return menuItemService.isMenuItemAvailable(id);
    }
}