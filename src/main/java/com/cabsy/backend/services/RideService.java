// src/main/java/com/cabsy/backend/services/RideService.java
package com.cabsy.backend.services;

import java.util.List;
import java.util.Optional;

import com.cabsy.backend.dtos.RideRequestDTO;
import com.cabsy.backend.dtos.RideResponseDTO;
import com.cabsy.backend.models.Ride;
import com.cabsy.backend.models.RideStatus;

public interface RideService {
    RideResponseDTO requestRide(Long userId, RideRequestDTO rideRequest);
    RideResponseDTO assignDriverToRide(Long rideId, Long driverId, Long cabId);
    RideResponseDTO updateRideStatus(Long rideId, RideStatus newStatus);
    Optional<RideResponseDTO> getRideById(Long rideId);
    List<RideResponseDTO> getRidesByUserId(Long userId);
    List<RideResponseDTO> getRidesByDriverId(Long driverId);
    List<Ride> getPreviousRidesByDriver(Long driverId);
    List<Ride> getAvailableRides();
    // Add methods for completing ride, cancelling ride, calculating fare, etc.
}