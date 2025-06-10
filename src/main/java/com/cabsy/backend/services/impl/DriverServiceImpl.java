// src/main/java/com/cabsy/backend/services/impl/DriverServiceImpl.java
package com.cabsy.backend.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cabsy.backend.dtos.DriverRegistrationDTO;
import com.cabsy.backend.dtos.DriverResponseDTO;
import com.cabsy.backend.models.Driver;
import com.cabsy.backend.models.DriverStatus;
import com.cabsy.backend.repositories.DriverRepository;
import com.cabsy.backend.services.DriverService;

import jakarta.persistence.EntityNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;

    public DriverServiceImpl(DriverRepository driverRepository, PasswordEncoder passwordEncoder) {
        this.driverRepository = driverRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public DriverResponseDTO registerDriver(DriverRegistrationDTO registrationDTO) {
        if (driverRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Driver with this email already exists");
        }
        if (driverRepository.findByPhoneNumber(registrationDTO.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("Driver with this phone number already exists");
        }
        if (driverRepository.findByLicenseNumber(registrationDTO.getLicenseNumber()).isPresent()) {
            throw new RuntimeException("Driver with this license number already exists");
        }

        Driver driver = new Driver();
        driver.setName(registrationDTO.getName());
        driver.setEmail(registrationDTO.getEmail());
        driver.setPhoneNumber(registrationDTO.getPhoneNumber());
        driver.setLicenseNumber(registrationDTO.getLicenseNumber());
        driver.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        driver.setStatus(DriverStatus.APPROVAL_PENDING);
        driver.setRating(0.0);

        Driver savedDriver = driverRepository.save(driver);
        return new DriverResponseDTO(
                savedDriver.getId(), savedDriver.getName(), savedDriver.getEmail(), savedDriver.getPhoneNumber(),
                savedDriver.getLicenseNumber(), savedDriver.getStatus(), savedDriver.getRating());
    }

    @Override
    public Optional<Driver> findDriverByEmail(String email) {
        return driverRepository.findByEmail(email);
    }

    @Override
    public Optional<DriverResponseDTO> getDriverById(Long id) {
        return driverRepository.findById(id)
                .map(driver -> new DriverResponseDTO(
                        driver.getId(), driver.getName(), driver.getEmail(), driver.getPhoneNumber(),
                        driver.getLicenseNumber(), driver.getStatus(), driver.getRating()));
    }

    @Override
    public List<DriverResponseDTO> getAllDrivers() {
        return driverRepository.findAll().stream()
                .map(driver -> new DriverResponseDTO(
                        driver.getId(), driver.getName(), driver.getEmail(), driver.getPhoneNumber(),
                        driver.getLicenseNumber(), driver.getStatus(), driver.getRating()))
                .collect(Collectors.toList());
    }

    // Removed: getAvailableDriversInArea method
    // Removed: updateDriverLocation method

    @Override
    @Transactional
    public DriverResponseDTO updateDriverStatus(Long driverId, DriverStatus newStatus) {
        return driverRepository.findById(driverId).map(driver -> {
            driver.setStatus(newStatus);
            Driver updatedDriver = driverRepository.save(driver);
            return new DriverResponseDTO(
                    updatedDriver.getId(), updatedDriver.getName(), updatedDriver.getEmail(),
                    updatedDriver.getPhoneNumber(),
                    updatedDriver.getLicenseNumber(), updatedDriver.getStatus(), updatedDriver.getRating());
        }).orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
    }

    
public Driver updateDriverProfile(Long id, DriverRegistrationDTO dto) {
     Driver driver = driverRepository.findById(id)
     .orElseThrow(() -> new EntityNotFoundException("Driver not found"));
    
     driver.setName(dto.getName());
     driver.setEmail(dto.getEmail());
     driver.setPhoneNumber(dto.getPhoneNumber());
     driver.setLicenseNumber(dto.getLicenseNumber());
    
     return driverRepository.save(driver);
     }
    
}