package com.nivakaran.guestservice.service;

import com.nivakaran.guestservice.dto.GuestRequest;
import com.nivakaran.guestservice.dto.GuestResponse;
import com.nivakaran.guestservice.model.Guest;
import com.nivakaran.guestservice.model.GuestType;
import com.nivakaran.guestservice.model.LoyaltyTier;
import com.nivakaran.guestservice.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestService {

    private final GuestRepository guestRepository;

    @Transactional
    public GuestResponse createGuest(GuestRequest request) {
        log.info("Creating new guest: {} {}", request.firstName(), request.lastName());

        // Check if email already exists
        if (guestRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Guest with email " + request.email() + " already exists");
        }

        String guestId = "G-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String createdBy = getCurrentUsername();

        Guest guest = Guest.builder()
                .guestId(guestId)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .dateOfBirth(request.dateOfBirth())
                .nationality(request.nationality())
                .passportNumber(request.passportNumber())
                .address(request.address())
                .city(request.city())
                .state(request.state())
                .country(request.country())
                .zipCode(request.zipCode())
                .guestType(request.guestType())
                .preferences(request.preferences())
                .specialRequests(request.specialRequests())
                .isActive(true)
                .loyaltyPoints(0)
                .loyaltyTier(LoyaltyTier.BRONZE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .notes(request.notes())
                .build();

        Guest savedGuest = guestRepository.save(guest);
        log.info("Guest created successfully with ID: {}", savedGuest.getGuestId());

        return mapToResponse(savedGuest);
    }

    public List<GuestResponse> getAllGuests() {
        log.info("Fetching all guests");
        return guestRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public GuestResponse getGuestById(Long id) {
        log.info("Fetching guest by id: {}", id);
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guest not found with id: " + id));
        return mapToResponse(guest);
    }

    public GuestResponse getGuestByGuestId(String guestId) {
        log.info("Fetching guest by guestId: {}", guestId);
        Guest guest = guestRepository.findByGuestId(guestId)
                .orElseThrow(() -> new RuntimeException("Guest not found with guestId: " + guestId));
        return mapToResponse(guest);
    }

    public GuestResponse getGuestByEmail(String email) {
        log.info("Fetching guest by email: {}", email);
        Guest guest = guestRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Guest not found with email: " + email));
        return mapToResponse(guest);
    }

    public List<GuestResponse> getGuestsByType(GuestType guestType) {
        log.info("Fetching guests by type: {}", guestType);
        return guestRepository.findByGuestType(guestType).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<GuestResponse> getGuestsByLoyaltyTier(LoyaltyTier loyaltyTier) {
        log.info("Fetching guests by loyalty tier: {}", loyaltyTier);
        return guestRepository.findByLoyaltyTier(loyaltyTier).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<GuestResponse> getActiveGuests() {
        log.info("Fetching active guests");
        return guestRepository.findByIsActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<GuestResponse> searchGuests(String keyword) {
        log.info("Searching guests with keyword: {}", keyword);
        return guestRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public GuestResponse updateGuest(Long id, GuestRequest request) {
        log.info("Updating guest: {}", id);

        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guest not found with id: " + id));

        // Check email uniqueness if changed
        if (!guest.getEmail().equals(request.email())) {
            if (guestRepository.findByEmail(request.email()).isPresent()) {
                throw new RuntimeException("Email " + request.email() + " is already in use");
            }
        }

        guest.setFirstName(request.firstName());
        guest.setLastName(request.lastName());
        guest.setEmail(request.email());
        guest.setPhone(request.phone());
        guest.setDateOfBirth(request.dateOfBirth());
        guest.setNationality(request.nationality());
        guest.setPassportNumber(request.passportNumber());
        guest.setAddress(request.address());
        guest.setCity(request.city());
        guest.setState(request.state());
        guest.setCountry(request.country());
        guest.setZipCode(request.zipCode());
        guest.setGuestType(request.guestType());
        guest.setPreferences(request.preferences());
        guest.setSpecialRequests(request.specialRequests());
        guest.setNotes(request.notes());
        guest.setUpdatedAt(LocalDateTime.now());

        Guest updatedGuest = guestRepository.save(guest);
        log.info("Guest updated successfully: {}", updatedGuest.getGuestId());

        return mapToResponse(updatedGuest);
    }

    @Transactional
    public void addLoyaltyPoints(String guestId, Integer points) {
        log.info("Adding {} loyalty points to guest: {}", points, guestId);

        Guest guest = guestRepository.findByGuestId(guestId)
                .orElseThrow(() -> new RuntimeException("Guest not found with guestId: " + guestId));

        Integer currentPoints = guest.getLoyaltyPoints() != null ? guest.getLoyaltyPoints() : 0;
        Integer newPoints = currentPoints + points;
        guest.setLoyaltyPoints(newPoints);

        // Update loyalty tier based on points
        LoyaltyTier newTier = calculateLoyaltyTier(newPoints);
        guest.setLoyaltyTier(newTier);
        guest.setUpdatedAt(LocalDateTime.now());

        guestRepository.save(guest);
        log.info("Loyalty points updated. New total: {}, Tier: {}", newPoints, newTier);
    }

    @Transactional
    public void deactivateGuest(Long id) {
        log.info("Deactivating guest: {}", id);

        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guest not found with id: " + id));

        guest.setIsActive(false);
        guest.setUpdatedAt(LocalDateTime.now());
        guestRepository.save(guest);

        log.info("Guest deactivated successfully");
    }

    @Transactional
    public void activateGuest(Long id) {
        log.info("Activating guest: {}", id);

        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guest not found with id: " + id));

        guest.setIsActive(true);
        guest.setUpdatedAt(LocalDateTime.now());
        guestRepository.save(guest);

        log.info("Guest activated successfully");
    }

    @Transactional
    public void deleteGuest(Long id) {
        log.info("Deleting guest: {}", id);

        if (!guestRepository.existsById(id)) {
            throw new RuntimeException("Guest not found with id: " + id);
        }

        guestRepository.deleteById(id);
        log.info("Guest deleted successfully");
    }

    private LoyaltyTier calculateLoyaltyTier(Integer points) {
        if (points >= 6000) {
            return LoyaltyTier.PLATINUM;
        } else if (points >= 3000) {
            return LoyaltyTier.GOLD;
        } else if (points >= 1000) {
            return LoyaltyTier.SILVER;
        } else {
            return LoyaltyTier.BRONZE;
        }
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "system";
    }

    private GuestResponse mapToResponse(Guest guest) {
        return new GuestResponse(
                guest.getId(),
                guest.getGuestId(),
                guest.getFirstName(),
                guest.getLastName(),
                guest.getEmail(),
                guest.getPhone(),
                guest.getDateOfBirth(),
                guest.getNationality(),
                guest.getPassportNumber(),
                guest.getAddress(),
                guest.getCity(),
                guest.getState(),
                guest.getCountry(),
                guest.getZipCode(),
                guest.getGuestType(),
                guest.getPreferences(),
                guest.getSpecialRequests(),
                guest.getIsActive(),
                guest.getLoyaltyPoints(),
                guest.getLoyaltyTier(),
                guest.getCreatedAt(),
                guest.getNotes()
        );
    }
}