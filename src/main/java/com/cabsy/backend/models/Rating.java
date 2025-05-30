// src/main/java/com/cabsy/backend/models/Rating.java
package com.cabsy.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings") // Maps to the 'ratings' table in SQL
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // One rating per ride from the user
    @JoinColumn(name = "ride_id", nullable = false, unique = true)
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser; // The user who gave the rating

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_driver_id", nullable = false)
    private Driver toDriver; // The driver who received the rating

    @Column(nullable = false)
    private Integer score; // Rating score, e.g., 1 to 5

    @Column(columnDefinition = "TEXT") // For longer text
    private String comments; // Optional detailed feedback

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}