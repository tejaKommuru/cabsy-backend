// src/main/java/com/cabsy/backend/repositories/DriverRepository.java
package com.cabsy.backend.repositories;

import com.cabsy.backend.models.Driver;
import com.cabsy.backend.models.DriverStatus; // Import the enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // Find a driver by their email
    Optional<Driver> findByEmail(String email);

    // Find a driver by their phone number
    Optional<Driver> findByPhoneNumber(String phoneNumber);

    // Find a driver by their license number
    Optional<Driver> findByLicenseNumber(String licenseNumber);

    // Find drivers by their status
    List<Driver> findByStatus(DriverStatus status);

    // Find available drivers within a certain radius (more complex, usually requires spatial queries)
    // For simplicity, here's an example for finding available drivers,
    // actual radius search would involve @Query or spatial libraries.
    List<Driver> findByStatusAndCurrentLocationLatBetweenAndCurrentLocationLonBetween(
        DriverStatus status, Double minLat, Double maxLat, Double minLon, Double maxLon);

    // Check if a driver with a given email already exists
    boolean existsByEmail(String email);

    // Check if a driver with a given phone number already exists
    boolean existsByPhoneNumber(String phoneNumber);

    // Check if a driver with a given license number already exists
    boolean existsByLicenseNumber(String licenseNumber);
}