package com.nivakaran.hotelservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "restaurant_tables")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTable {
    @Id
    private String id;
    private String tableNumber;
    private Integer capacity;
    private TableLocation location;
    private TableStatus status;
    private BigDecimal reservationFee;
    private Boolean isWindowSeat;
    private String description;
}