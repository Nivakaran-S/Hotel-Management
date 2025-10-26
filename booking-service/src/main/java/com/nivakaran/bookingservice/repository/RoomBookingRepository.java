package com.nivakaran.bookingservice.repository;

import com.nivakaran.bookingservice.model.BookingStatus;
import com.nivakaran.bookingservice.model.RoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {
    Optional<RoomBooking> findByBookingNumber(String bookingNumber);
    Optional<RoomBooking> findByIdempotencyKey(String idempotencyKey);
    List<RoomBooking> findByGuestEmail(String guestEmail);
    List<RoomBooking> findByStatus(BookingStatus status);
    List<RoomBooking> findByRoomIdAndStatus(String roomId, BookingStatus status);

    @Query("SELECT rb FROM RoomBooking rb WHERE rb.roomId = :roomId " +
            "AND rb.status NOT IN ('CANCELLED', 'CHECKED_OUT', 'NO_SHOW') " +
            "AND ((rb.checkInDate <= :checkOutDate AND rb.checkOutDate >= :checkInDate))")
    List<RoomBooking> findConflictingBookings(String roomId, LocalDate checkInDate, LocalDate checkOutDate);
}