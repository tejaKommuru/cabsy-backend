// src/main/java/com/cabsy/backend/controllers/CabController.java
package com.cabsy.backend.controllers;

import com.cabsy.backend.models.Cab;
import com.cabsy.backend.models.CabStatus;
import com.cabsy.backend.services.CabService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cabs") // Base path for all cab-related endpoints
public class CabController {

    private final CabService cabService;

    @Autowired
    public CabController(CabService cabService) {
        this.cabService = cabService;
    }

    // POST /api/cabs
    // Request Body Example:
    // {
    // "driver": {"id": 1}, // Provide existing driver ID
    // "make": "Toyota",
    // "model": "Innova",
    // "licensePlate": "KA01AB1234",
    // "vehicleType": "SUV",
    // "capacity": 7,
    // "color": "White",
    // "manufacturingYear": "2020",
    // "status": "IN_SERVICE", // Optional, will default if not provided in service
    // "insuranceDetails": "Policy123",
    // "registrationDetails": "Reg456"
    // }
    @PostMapping
    public ResponseEntity<Cab> createCab(@RequestBody Cab cab) {
        try {
            Cab createdCab = cabService.createCab(cab);
            return new ResponseEntity<>(createdCab, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Handle cases where driver ID is missing or invalid
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/cabs
    @GetMapping
    public ResponseEntity<List<Cab>> getAllCabs() {
        List<Cab> cabs = cabService.getAllCabs();
        return new ResponseEntity<>(cabs, HttpStatus.OK);
    }

    // GET /api/cabs/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Cab> getCabById(@PathVariable Long id) {
        Optional<Cab> cab = cabService.getCabById(id);
        return cab.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // PUT /api/cabs/{id}
    // Request Body is similar to POST
    @PutMapping("/{id}")
    public ResponseEntity<Cab> updateCab(@PathVariable Long id, @RequestBody Cab cabDetails) {
        try {
            Cab updatedCab = cabService.updateCab(id, cabDetails);
            return new ResponseEntity<>(updatedCab, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE /api/cabs/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCab(@PathVariable Long id) {
        try {
            cabService.deleteCab(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Custom Endpoints for filtering/searching ---

    // GET /api/cabs/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Cab>> getCabsByStatus(@PathVariable String status) {
        try {
            CabStatus cabStatus = CabStatus.valueOf(status.toUpperCase()); // Convert string to enum
            List<Cab> cabs = cabService.getCabsByStatus(cabStatus);
            return new ResponseEntity<>(cabs, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Invalid status string
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/cabs/driver/{driverId}
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Cab>> getCabsByDriver(@PathVariable Long driverId) {
        List<Cab> cabs = cabService.getCabsByDriver(driverId);
        return new ResponseEntity<>(cabs, HttpStatus.OK);
    }

    // GET /api/cabs/license/{licensePlate}
    @GetMapping("/license/{licensePlate}")
    public ResponseEntity<Cab> getCabByLicensePlate(@PathVariable String licensePlate) {
        Optional<Cab> cab = cabService.getCabByLicensePlate(licensePlate);
        return cab.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}