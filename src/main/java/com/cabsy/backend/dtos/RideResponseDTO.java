// src/main/java/com/cabsy/backend/dtos/RideResponseDTO.java
package com.cabsy.backend.dtos;

import java.time.LocalDateTime;

import com.cabsy.backend.models.RideStatus;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideResponseDTO {
    private Long id;
    private Long userId;
    private Long driverId; // Can be null
    private Double pickupLat;
    private Double pickupLon;
    private Double destinationLat;
    private Double destinationLon;
    private String pickupAddress;
    private String destinationAddress;
    private RideStatus status;
    private Double estimatedFare;
    private Double actualFare;
    private LocalDateTime requestTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    // You might include PaymentSummaryDTO and RatingSummaryDTO here later
    
    // Add user info
    private String userName;
    private String userEmail;
    private String userPhone;

}