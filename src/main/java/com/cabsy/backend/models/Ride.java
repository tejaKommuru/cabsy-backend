// src/main/java/com/cabsy/backend/models/Ride.java
package com.cabsy.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides") // Maps to the 'rides' table in SQL
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The user who booked the ride

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id") // Can be null if no driver assigned yet
    private Driver driver; // The driver assigned to the ride

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id") // Can be null if no vehicle assigned yet
    private Cab vehicle; // The specific vehicle used for the ride

    @Column(name = "pickup_location_lat", nullable = false)
    private Double pickupLocationLat;

    @Column(name = "pickup_location_lon", nullable = false)
    private Double pickupLocationLon;

    @Column(name = "dropoff_location_lat", nullable = false)
    private Double dropoffLocationLat;

    @Column(name = "dropoff_location_lon", nullable = false)
    private Double dropoffLocationLon;

    @Column(name = "pickup_address", nullable = false, length = 255)
    private String pickupAddress;

    @Column(name = "dropoff_address", nullable = false, length = 255)
    private String dropoffAddress;

    @Column(name = "booking_time", nullable = false, updatable = false)
    private LocalDateTime bookingTime;

    @Column(name = "start_time") // Can be null until ride starts
    private LocalDateTime startTime;

    @Column(name = "end_time") // Can be null until ride ends
    private LocalDateTime endTime;

    @Column(name = "fare", nullable = false, precision = 10, scale = 2)
    private BigDecimal fare;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RideStatus status; // e.g., BOOKED, IN_PROGRESS, COMPLETED

    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; // e.g., PENDING, PAID, FAILED

    @PrePersist
    protected void onCreate() {
        this.bookingTime = LocalDateTime.now();
        if (this.status == null) {
            this.status = RideStatus.BOOKED; // Default status for new rides
        }
        if (this.paymentStatus == null) {
            this.paymentStatus = PaymentStatus.PENDING; // Default payment status
        }
        // Fare might be calculated by service later, or set to 0.0 initially
        if (this.fare == null) {
            this.fare = BigDecimal.ZERO;
        }
    }
}