// src/main/java/com/cabsy/backend/controllers/DriverController.java
package com.cabsy.backend.controllers;

import com.cabsy.backend.dtos.ApiResponse;
import com.cabsy.backend.dtos.DriverResponseDTO;
import com.cabsy.backend.models.DriverStatus;
import com.cabsy.backend.services.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverResponseDTO>> getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id)
                .map(driverDTO -> ResponseEntity.ok(ApiResponse.success("Driver fetched successfully", driverDTO)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Driver not found", "Driver with ID " + id + " does not exist")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DriverResponseDTO>>> getAllDrivers() {
        List<DriverResponseDTO> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(ApiResponse.success("Drivers fetched successfully", drivers));
    }

    // Removed: getAvailableDrivers endpoint
    // Removed: updateDriverLocation endpoint

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DriverResponseDTO>> updateDriverStatus(
            @PathVariable Long id,
            @RequestParam DriverStatus status) {
        try {
            DriverResponseDTO updatedDriver = driverService.updateDriverStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Driver status updated successfully", updatedDriver));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to update driver status", e.getMessage()));
        }
    }
}