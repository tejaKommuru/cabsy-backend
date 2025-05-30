// src/main/java/com/cabsy/backend/repositories/CabRepository.java
package com.cabsy.backend.repositories;

import com.cabsy.backend.models.Cab;
import com.cabsy.backend.models.CabStatus; // Import the enum
import com.cabsy.backend.models.Driver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CabRepository extends JpaRepository<Cab, Long> {

    // Find cabs by their status
    List<Cab> findByStatus(CabStatus status);

    // Find cabs by vehicle type
    List<Cab> findByVehicleType(String vehicleType);

    // Find a cab by its license plate (assuming it's unique)
    Optional<Cab> findByLicensePlate(String licensePlate);

    // Find cabs assigned to a specific driver (using driver object or driver's ID)
    List<Cab> findByDriverId(Long driverId);
    List<Cab> findByDriver(Driver driver); // Find cabs by the Driver entity

    // Find cabs by driver and status
    List<Cab> findByDriverAndStatus(Driver driver, CabStatus status);

    // Check if a cab with a given license plate already exists
    boolean existsByLicensePlate(String licensePlate);
}