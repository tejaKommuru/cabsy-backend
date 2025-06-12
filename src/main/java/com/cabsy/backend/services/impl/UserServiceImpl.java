// src/main/java/com/cabsy/backend/services/impl/UserServiceImpl.java
package com.cabsy.backend.services.impl;

import com.cabsy.backend.dtos.UserRegistrationDTO;
import com.cabsy.backend.dtos.UserResponseDTO;
import com.cabsy.backend.models.User;
import com.cabsy.backend.repositories.UserRepository;
import com.cabsy.backend.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder; // New import
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional; // For transactional operations
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        // Check if user with email or phone already exists
        if (userRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists"); // TODO: Custom exception
        }
        if (userRepository.findByPhoneNumber(registrationDTO.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("User with this phone number already exists"); // TODO: Custom exception
        }

        User user = new User();
        user.setName(registrationDTO.getName());
        user.setEmail(registrationDTO.getEmail());
        user.setPhoneNumber(registrationDTO.getPhoneNumber());
        // HASH THE PASSWORD BEFORE SAVING
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        User savedUser = userRepository.save(user);
        return new UserResponseDTO(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getPhoneNumber(), savedUser.getRating());
    }
   
    @Override
    @Transactional
    public void updatePasswordByEmail(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // 1. Validate new password (add more rules if needed, e.g., complexity)
        if (newPassword == null || newPassword.length() < 6) { // Min 6 chars as per frontend
            throw new IllegalArgumentException("New password must be at least 6 characters long.");
        }
        // IMPORTANT: In a real "forgot password" flow with a token, you typically don't check
        // if new password is same as old, as the user might not remember it.
        // For a dummy project, if you want to enforce it, you can keep this check.
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the old password.");
        }

        // 2. Hash and set new password
        user.setPassword(passwordEncoder.encode(newPassword));

        // 3. Save the updated user
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateName(String name, Long id) { // Now returns void
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setName(name);
            userRepository.save(existingUser);
            // No return value needed here
        } else {
            // It's still crucial to throw an exception if the user isn't found
            throw new RuntimeException("User not found with id: " + id);
        }
    }


    @Override
    @Transactional
    public void updateEmail(String newEmail, Long userId) {
        // 1. Validate email format (simple regex)
        if (newEmail == null || !Pattern.matches("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", newEmail)) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        // 2. Check if the user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 3. Check if the new email is already taken by another user
        // Only check if the new email is different from the current one
        if (!user.getEmail().equalsIgnoreCase(newEmail)) {
            if (userRepository.findByEmail(newEmail).isPresent()) {
                throw new IllegalArgumentException("Email address is already in use by another account.");
            }
        }

        // 4. Update the email and save
        user.setEmail(newEmail);
        userRepository.save(user); // This will also trigger @PreUpdate for updatedAt
    }

    @Override
    public void updatePhoneNumber(String newPhoneNumber,Long userId ){
        if(newPhoneNumber == null || newPhoneNumber.trim().isEmpty()){
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }

        if (!Pattern.matches("^\\+?[0-9]{7,15}$", newPhoneNumber)) { // Adjust regex as per your requirements
            throw new IllegalArgumentException("Invalid phone number format. Must contain only digits, optionally starting with '+'.");
        }
        User user=userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
          // 3. Check if the new phone number is already taken by another user
        // Only check if the new number is different from the current one
        if (!user.getPhoneNumber().equals(newPhoneNumber)) {
            if (userRepository.findByPhoneNumber(newPhoneNumber).isPresent()) { // You'll need this method in UserRepository
                throw new IllegalArgumentException("Phone number is already in use by another account.");
            }
        }
         // 4. Update the phone number and save
         user.setPhoneNumber(newPhoneNumber);
         userRepository.save(user); // This will also trigger @PreUpdate for updatedAt

    }


    @Override
    @Transactional
    public void updatePassword(String oldPassword, String newPassword, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 1. Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password.");
        }

        // 2. Validate new password (add more rules if needed, e.g., complexity)
        if (newPassword == null || newPassword.length() < 8) { // Example: min 8 characters
            throw new IllegalArgumentException("New password must be at least 8 characters long.");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the old password.");
        }

        // 3. Hash and set new password
        user.setPassword(passwordEncoder.encode(newPassword));

        // 4. Save the updated user
        userRepository.save(user);
    }


    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(), user.getRating()));
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(), user.getRating()))
                .collect(Collectors.toList());
    }

    

   
}