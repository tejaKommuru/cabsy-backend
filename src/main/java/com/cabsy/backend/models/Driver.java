// src/main/java/com/cabsy/backend/models/Driver.java
package com.cabsy.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers") // Maps to the 'drivers' table in SQL
@Data // Generates getters, setters, toString, equals, hashCode from Lombok
@NoArgsConstructor // Generates a no-argument constructor from Lombok
@AllArgsConstructor // Generates a constructor with all fields from Lombok
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    // This is the 'status' field that caused the error.
    // Ensure the field name is 'status' and it's of type DriverStatus.
    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // Stores the enum value as a string (e.g., "AVAILABLE") in the DB
    private DriverStatus status; // e.g., AVAILABLE, OCCUPIED, OFFLINE

    @Column(nullable = false)
    private Double rating; // Average rating for the driver

    // Real-time location of the driver
    @Column(name = "current_location_lat")
    private Double currentLocationLat;

    @Column(name = "current_location_lon")
    private Double currentLocationLon;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // JPA Lifecycle Callbacks for automatic timestamp management
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Set default values for new drivers if not explicitly provided
        if (this.rating == null) {
            this.rating = 0.0; // Default rating for new drivers
        }
        if (this.status == null) {
            this.status = DriverStatus.APPROVAL_PENDING; // Default status for new drivers
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}