// src/main/java/com/cabsy/backend/repositories/RideRepository.java
package com.cabsy.backend.repositories;

import com.cabsy.backend.models.Ride;
import com.cabsy.backend.models.RideStatus; // Import the enum
import com.cabsy.backend.models.User;
import com.cabsy.backend.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    // Find rides by user
    List<Ride> findByUser(User user);

    // Find rides by driver
    List<Ride> findByDriver(Driver driver);

    // Find rides by status
    List<Ride> findByStatus(RideStatus status);

    // Find rides by user and status
    List<Ride> findByUserAndStatus(User user, RideStatus status);

    // Find rides by driver and status
    List<Ride> findByDriverAndStatus(Driver driver, RideStatus status);

    // Find active rides (e.g., BOOKED, ACCEPTED, IN_PROGRESS) for a specific user
    List<Ride> findByUserAndStatusIn(User user, List<RideStatus> statuses);

    // Find active rides for a specific driver
    List<Ride> findByDriverAndStatusIn(Driver driver, List<RideStatus> statuses);

    // Find rides booked within a specific time range
    List<Ride> findByBookingTimeBetween(LocalDateTime start, LocalDateTime end);

    // Find completed rides for a user
    List<Ride> findByUserAndStatusOrderByEndTimeDesc(User user, RideStatus status);

    // Find completed rides for a driver
    List<Ride> findByDriverAndStatusOrderByEndTimeDesc(Driver driver, RideStatus status);
}