package com.nivakaran.hotelservice.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record TableAvailabilityRequest(
        LocalDate date,
        LocalTime time,
        Integer guestCount
) {}