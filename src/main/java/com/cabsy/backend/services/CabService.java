// src/main/java/com/cabsy/backend/services/CabService.java
package com.cabsy.backend.services;

import com.cabsy.backend.models.Cab;
import com.cabsy.backend.models.CabStatus;
import com.cabsy.backend.models.Driver;
import com.cabsy.backend.repositories.CabRepository;
import com.cabsy.backend.repositories.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CabService {

    private final CabRepository cabRepository;
    private final DriverRepository driverRepository; // Needed for driver relationship

    @Autowired
    public CabService(CabRepository cabRepository, DriverRepository driverRepository) {
        this.cabRepository = cabRepository;
        this.driverRepository = driverRepository;
    }

    // --- Create a new Cab ---
    public Cab createCab(Cab cab) {
        // Business logic: Ensure the associated driver exists before saving the cab
        if (cab.getDriver() == null || cab.getDriver().getId() == null) {
            throw new IllegalArgumentException("Driver ID must be provided to create a cab.");
        }
        Optional<Driver> existingDriver = driverRepository.findById(cab.getDriver().getId());
        if (existingDriver.isEmpty()) {
            throw new IllegalArgumentException("Driver with ID " + cab.getDriver().getId() + " not found.");
        }
        // Set the managed Driver entity to the Cab
        cab.setDriver(existingDriver.get());
        
        // You might want to set a default status for new cabs, e.g., PENDING_APPROVAL
        if (cab.getStatus() == null) {
            cab.setStatus(CabStatus.PENDING_APPROVAL); // Or IN_SERVICE, depending on your flow
        }
        return cabRepository.save(cab);
    }

    // --- Retrieve all Cabs ---
    public List<Cab> getAllCabs() {
        return cabRepository.findAll();
    }

    // --- Retrieve a Cab by ID ---
    public Optional<Cab> getCabById(Long id) {
        return cabRepository.findById(id);
    }

    // --- Update an existing Cab ---
    public Cab updateCab(Long id, Cab cabDetails) {
        Optional<Cab> optionalCab = cabRepository.findById(id);
        if (optionalCab.isPresent()) {
            Cab existingCab = optionalCab.get();
            // Update fields that can be changed
            existingCab.setMake(cabDetails.getMake());
            existingCab.setModel(cabDetails.getModel());
            existingCab.setLicensePlate(cabDetails.getLicensePlate());
            existingCab.setVehicleType(cabDetails.getVehicleType());
            existingCab.setCapacity(cabDetails.getCapacity());
            existingCab.setColor(cabDetails.getColor());
            existingCab.setManufacturingYear(cabDetails.getManufacturingYear());
            existingCab.setInsuranceDetails(cabDetails.getInsuranceDetails());
            existingCab.setRegistrationDetails(cabDetails.getRegistrationDetails());
            
            // Only update status if provided, or if it's a valid transition
            if (cabDetails.getStatus() != null) {
                existingCab.setStatus(cabDetails.getStatus());
            }

            // If the driver is being updated, validate its existence
            if (cabDetails.getDriver() != null && cabDetails.getDriver().getId() != null) {
                Optional<Driver> newDriver = driverRepository.findById(cabDetails.getDriver().getId());
                if (newDriver.isEmpty()) {
                    throw new IllegalArgumentException("New Driver with ID " + cabDetails.getDriver().getId() + " not found.");
                }
                existingCab.setDriver(newDriver.get());
            }
            
            return cabRepository.save(existingCab);
        } else {
            throw new RuntimeException("Cab not found with id " + id);
        }
    }

    // --- Delete a Cab ---
    public void deleteCab(Long id) {
        cabRepository.deleteById(id);
    }

    // --- Custom Service Methods based on Repository Queries ---
    public List<Cab> getCabsByStatus(CabStatus status) {
        return cabRepository.findByStatus(status);
    }

    public List<Cab> getCabsByVehicleType(String vehicleType) {
        return cabRepository.findByVehicleType(vehicleType);
    }

    public Optional<Cab> getCabByLicensePlate(String licensePlate) {
        return cabRepository.findByLicensePlate(licensePlate);
    }

    public List<Cab> getCabsByDriver(Long driverId) {
        return cabRepository.findByDriverId(driverId);
    }
}