package com.cabsy.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column; // For specific column mappings
import jakarta.persistence.ManyToOne; // For relationships
import jakarta.persistence.JoinColumn; // For foreign key column
import jakarta.persistence.Enumerated; // For enum types
import jakarta.persistence.EnumType; // For enum types

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "vehicles") 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cab { 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

 
    @ManyToOne // Many cabs can belong to one driver
    @JoinColumn(name = "driver_id", nullable = false) // This creates a foreign key column named 'driver_id'
    private Driver driver; // Reference to the Driver entity

    @Column(nullable = false, length = 50)
    private String make; // e.g., Toyota, Maruti Suzuki

    @Column(nullable = false, length = 50)
    private String model; // e.g., Innova, Swift Dzire

    @Column(nullable = false, unique = true, length = 20)
    private String licensePlate; // e.g., KA01AB1234

    @Column(nullable = false, length = 50)
    private String vehicleType; // e.g., Sedan, SUV, Hatchback (was cabType)

    @Column(nullable = false)
    private int capacity; // Number of passengers

    @Column(length = 30)
    private String color;

    @Column(length = 4)
    private String manufacturingYear; // Use String if you only care about the year

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // Stores enum as a string in the database
    private CabStatus status; // Replaced 'available' with a more detailed status enum

    // --- Removed currentLocationLat and currentLocationLon ---
    // These fields are typically associated with the Driver's real-time location,
    // as it's the driver who moves the cab and whose current position is relevant for ride matching.
    // You'd track this on the Driver entity or a separate DriverLocation entity
    // which is updated frequently.

    // --- Removed driverName ---
    // This is now redundant because you have a direct relationship to the Driver entity.
    // You can access the driver's name via `cab.getDriver().getName()`.

    // --- Optional additional fields based on our LLD ---
    @Column(length = 255)
    private String insuranceDetails; // e.g., policy number, expiry date

    @Column(length = 255)
    private String registrationDetails; // e.g., registration number, expiry date

    // You might also consider fields for vehicle images, last service date, etc., as your system grows.
}