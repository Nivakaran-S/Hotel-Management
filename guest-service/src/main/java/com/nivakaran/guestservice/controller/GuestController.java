package com.nivakaran.guestservice.controller;

import com.nivakaran.guestservice.dto.GuestRequest;
import com.nivakaran.guestservice.dto.GuestResponse;
import com.nivakaran.guestservice.model.GuestType;
import com.nivakaran.guestservice.model.LoyaltyTier;
import com.nivakaran.guestservice.service.GuestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public GuestResponse createGuest(@Valid @RequestBody GuestRequest request) {
        return guestService.createGuest(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<GuestResponse> getAllGuests() {
        return guestService.getAllGuests();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public GuestResponse getGuestById(@PathVariable Long id) {
        return guestService.getGuestById(id);
    }

    @GetMapping("/guest-id/{guestId}")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public GuestResponse getGuestByGuestId(@PathVariable String guestId) {
        return guestService.getGuestByGuestId(guestId);
    }

    @GetMapping("/email/{email}")
    public GuestResponse getGuestByEmail(@PathVariable String email) {
        return guestService.getGuestByEmail(email);
    }

    @GetMapping("/type/{guestType}")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<GuestResponse> getGuestsByType(@PathVariable GuestType guestType) {
        return guestService.getGuestsByType(guestType);
    }

    @GetMapping("/loyalty-tier/{loyaltyTier}")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<GuestResponse> getGuestsByLoyaltyTier(@PathVariable LoyaltyTier loyaltyTier) {
        return guestService.getGuestsByLoyaltyTier(loyaltyTier);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<GuestResponse> getActiveGuests() {
        return guestService.getActiveGuests();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<GuestResponse> searchGuests(@RequestParam String keyword) {
        return guestService.searchGuests(keyword);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public GuestResponse updateGuest(@PathVariable Long id, @Valid @RequestBody GuestRequest request) {
        return guestService.updateGuest(id, request);
    }

    @PatchMapping("/{guestId}/loyalty-points")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLoyaltyPoints(@PathVariable String guestId, @RequestParam Integer points) {
        guestService.addLoyaltyPoints(guestId, points);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateGuest(@PathVariable Long id) {
        guestService.deactivateGuest(id);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateGuest(@PathVariable Long id) {
        guestService.activateGuest(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGuest(@PathVariable Long id) {
        guestService.deleteGuest(id);
    }
}