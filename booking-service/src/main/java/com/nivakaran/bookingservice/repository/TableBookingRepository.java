package com.nivakaran.bookingservice.repository;

import com.nivakaran.bookingservice.model.BookingStatus;
import com.nivakaran.bookingservice.model.TableBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TableBookingRepository extends JpaRepository<TableBooking, Long> {
    Optional<TableBooking> findByBookingNumber(String bookingNumber);
    List<TableBooking> findByGuestEmail(String guestEmail);
    List<TableBooking> findByStatus(BookingStatus status);
    List<TableBooking> findByTableIdAndStatus(String tableId, BookingStatus status);

    @Query("SELECT tb FROM TableBooking tb WHERE tb.tableId = :tableId " +
            "AND tb.reservationDate = :reservationDate " +
            "AND tb.status NOT IN ('CANCELLED', 'COMPLETED', 'NO_SHOW') " +
            "AND ABS(FUNCTION('TIMESTAMPDIFF', MINUTE, tb.reservationTime, :reservationTime)) < 120")
    List<TableBooking> findConflictingBookings(String tableId, LocalDate reservationDate, LocalTime reservationTime);
}