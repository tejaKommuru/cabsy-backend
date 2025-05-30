// src/main/java/com/cabsy/backend/models/RideStatus.java
package com.cabsy.backend.models;

public enum RideStatus {
    BOOKED, // Ride requested by user
    ACCEPTED, // Driver has accepted the ride
    DRIVER_ON_THE_WAY, // Driver is en route to pickup location
    ARRIVED_AT_PICKUP, // Driver has arrived at pickup location
    IN_PROGRESS, // Ride has started (user is in the cab)
    COMPLETED, // Ride successfully finished
    CANCELLED_BY_USER, // User cancelled the ride
    CANCELLED_BY_DRIVER, // Driver cancelled the ride
    NO_DRIVER_FOUND // No driver available to accept the ride
}