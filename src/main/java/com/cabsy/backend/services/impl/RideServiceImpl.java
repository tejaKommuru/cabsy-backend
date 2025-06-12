// src/main/java/com/cabsy/backend/services/impl/RideServiceImpl.java
package com.cabsy.backend.services.impl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cabsy.backend.dtos.RideRequestDTO;
import com.cabsy.backend.dtos.RideResponseDTO;
import com.cabsy.backend.models.Driver;
import com.cabsy.backend.models.Ride;
import com.cabsy.backend.models.RideStatus;
import com.cabsy.backend.models.User;
import com.cabsy.backend.repositories.DriverRepository;
import com.cabsy.backend.repositories.RideRepository;
import com.cabsy.backend.repositories.UserRepository;
import com.cabsy.backend.services.RideService;

@Service
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    

    public RideServiceImpl(RideRepository rideRepository, UserRepository userRepository,
                           DriverRepository driverRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    @Transactional
    public RideResponseDTO requestRide(Long userId, RideRequestDTO rideRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId)); // TODO: Custom exception

        Ride ride = new Ride();
        ride.setUser(user);
        ride.setPickupLat(rideRequest.getPickupLat());
        ride.setPickupLon(rideRequest.getPickupLon());
        ride.setDestinationLat(rideRequest.getDestinationLat());
        ride.setDestinationLon(rideRequest.getDestinationLon());
        ride.setPickupAddress(rideRequest.getPickupAddress());
        ride.setDestinationAddress(rideRequest.getDestinationAddress());
        ride.setStatus(RideStatus.REQUESTED);
        ride.setEstimatedFare(calculateEstimatedFare(rideRequest.getPickupLat(), rideRequest.getPickupLon(),
                                                    rideRequest.getDestinationLat(), rideRequest.getDestinationLon()));
        ride.setRequestTime(LocalDateTime.now());

        Ride savedRide = rideRepository.save(ride);
        

        // TODO: Implement logic to find and assign a driver here or in a separate "matching" service
        // For now, we'll just return the requested ride.
        // In a real app, this would trigger driver notification and assignment.

        return mapToRideResponseDTO(savedRide);
    }

    @Override
    @Transactional
    public RideResponseDTO assignDriverToRide(Long rideId, Long driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        // Basic checks:
        if (ride.getDriver() != null) {
            throw new RuntimeException("Ride already has a driver/cab assigned.");
        }
    
        ride.setDriver(driver);
        ride.setStatus(RideStatus.ACCEPTED);

        driverRepository.save(driver); // Update driver status
        Ride updatedRide = rideRepository.save(ride);

        return mapToRideAssignResponseDTO(updatedRide);
    }

    @Override
    @Transactional
    public RideResponseDTO updateRideStatus(Long rideId, RideStatus newStatus) {
        return rideRepository.findById(rideId).map(ride -> {
            // Add state transition validation here if needed (e.g., cannot go from COMPLETED to REQUESTED)
            ride.setStatus(newStatus);

            if (newStatus == RideStatus.IN_PROGRESS && ride.getStartTime() == null) {
                ride.setStartTime(LocalDateTime.now());
                // TODO: Update driver/cab status if not already ON_TRIP/OCCUPIED
            } else if (newStatus == RideStatus.COMPLETED && ride.getEndTime() == null) {
                ride.setEndTime(LocalDateTime.now());
                // Calculate final fare based on actual time/distance (if different from estimated)
                if (ride.getActualFare() == null) {
                    // This is a placeholder; actual calculation based on route taken.
                    ride.setActualFare(ride.getEstimatedFare() * 1.05); // Example: 5% more than estimated
                }
        
            }

            Ride updatedRide = rideRepository.save(ride);
            return mapToRideResponseDTO(updatedRide);
        }).orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId));
    }

    @Override
    @Transactional
    public Optional<RideResponseDTO> getRideById(Long rideId) {
        return rideRepository.findById(rideId).map(this::mapToRideResponseDTO);
    }

    @Override
    public List<RideResponseDTO> getRidesByUserId(Long userId) {
        return rideRepository.findByUserId(userId).stream()
                .map(this::mapToRideResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<RideResponseDTO> getPreviousRidesByDriverId(Long driverId) {
        return rideRepository.findByDriverIdAndStatus(driverId, RideStatus.COMPLETED).stream()
                .map(this::mapToRideAssignResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public List<RideResponseDTO> getAvailableRides() {
     return rideRepository.findByStatus(RideStatus.REQUESTED).stream()
     .map(this::mapToRideAssignResponseDTO)
     .collect(Collectors.toList());
     }
    
    // --- Helper methods ---

    private Double calculateEstimatedFare(Double pickupLat, Double pickupLon, Double destLat, Double destLon) {
        // TODO: Implement a more sophisticated fare calculation based on distance, time, traffic, etc.
        // For now, a simple fixed rate per km (e.g., based on Haversine distance).
        // This is a placeholder.
        double distanceKm = calculateHaversineDistance(pickupLat, pickupLon, destLat, destLon);
        return distanceKm * 15; // Example: 10 units per km
    }

    private double calculateHaversineDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // Radius of Earth in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private RideResponseDTO mapToRideResponseDTO(Ride ride) {
        RideResponseDTO dto = new RideResponseDTO();
        dto.setId(ride.getId());
        dto.setUserId(ride.getUser().getId());
        dto.setDriverId(ride.getDriver() != null ? ride.getDriver().getId() : null);
        dto.setPickupLat(ride.getPickupLat());
        dto.setPickupLon(ride.getPickupLon());
        dto.setDestinationLat(ride.getDestinationLat());
        dto.setDestinationLon(ride.getDestinationLon());
        dto.setPickupAddress(ride.getPickupAddress());
        dto.setDestinationAddress(ride.getDestinationAddress());
        dto.setStatus(ride.getStatus());
        dto.setEstimatedFare(ride.getEstimatedFare());
        dto.setActualFare(ride.getActualFare());
        dto.setRequestTime(ride.getRequestTime());
        dto.setStartTime(ride.getStartTime());
        dto.setEndTime(ride.getEndTime());
    
        return dto;
    }

    private RideResponseDTO mapToRideAssignResponseDTO(Ride ride) {
        RideResponseDTO dto = new RideResponseDTO();
        dto.setId(ride.getId());
        dto.setUserId(ride.getUser().getId());
        dto.setDriverId(ride.getDriver() != null ? ride.getDriver().getId() : null);
        dto.setPickupLat(ride.getPickupLat());
        dto.setPickupLon(ride.getPickupLon());
        dto.setDestinationLat(ride.getDestinationLat());
        dto.setDestinationLon(ride.getDestinationLon());
        dto.setPickupAddress(ride.getPickupAddress());
        dto.setDestinationAddress(ride.getDestinationAddress());
        dto.setStatus(ride.getStatus());
        dto.setEstimatedFare(ride.getEstimatedFare());
        dto.setActualFare(ride.getActualFare());
        dto.setRequestTime(ride.getRequestTime());
        dto.setStartTime(ride.getStartTime());
        dto.setEndTime(ride.getEndTime());

         // ✅ Add user details
         User user = ride.getUser();
         if (user != null) {
             dto.setUserName(user.getName());
             dto.setUserEmail(user.getEmail());
             dto.setUserPhone(user.getPhoneNumber());
         }
     
        return dto;
    }
}