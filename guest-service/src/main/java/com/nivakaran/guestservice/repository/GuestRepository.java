package com.nivakaran.guestservice.repository;

import com.nivakaran.guestservice.model.Guest;
import com.nivakaran.guestservice.model.GuestType;
import com.nivakaran.guestservice.model.LoyaltyTier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    Optional<Guest> findByGuestId(String guestId);
    Optional<Guest> findByEmail(String email);
    List<Guest> findByGuestType(GuestType guestType);
    List<Guest> findByLoyaltyTier(LoyaltyTier loyaltyTier);
    List<Guest> findByIsActive(Boolean isActive);
    List<Guest> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}