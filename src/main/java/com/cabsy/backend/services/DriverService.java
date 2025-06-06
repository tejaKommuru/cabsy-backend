// src/main/java/com/cabsy/backend/services/DriverService.java
package com.cabsy.backend.services;

import java.util.List;
import java.util.Optional;

import com.cabsy.backend.dtos.ChangePasswordRequest;
import com.cabsy.backend.dtos.DriverRegistrationDTO;
import com.cabsy.backend.dtos.DriverResponseDTO;
import com.cabsy.backend.models.Driver;
import com.cabsy.backend.models.DriverStatus;

public interface DriverService {
    DriverResponseDTO registerDriver(DriverRegistrationDTO registrationDTO);
    Optional<Driver> findDriverByEmail(String email);
    Optional<DriverResponseDTO> getDriverById(Long id);
    List<DriverResponseDTO> getAllDrivers();
    // Removed: List<DriverResponseDTO> getAvailableDriversInArea(Double lat, Double lon, Double radiusKm);
    DriverResponseDTO updateDriverStatus(Long driverId, DriverStatus newStatus);
    // Removed: DriverResponseDTO updateDriverLocation(Long driverId, Double lat, Double lon);
    String changePassword(ChangePasswordRequest requestDTO);
}