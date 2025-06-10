// src/main/java/com/cabsy/backend/controllers/AuthController.java
package com.cabsy.backend.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cabsy.backend.dtos.ApiResponse;
import com.cabsy.backend.dtos.DriverRegistrationDTO;
import com.cabsy.backend.dtos.DriverResponseDTO;
import com.cabsy.backend.dtos.LoginDTO;
import com.cabsy.backend.dtos.UserRegistrationDTO;
import com.cabsy.backend.dtos.UserResponseDTO;
import com.cabsy.backend.services.DriverService;
import com.cabsy.backend.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final DriverService driverService;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder for login

    public AuthController(UserService userService, DriverService driverService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.driverService = driverService;
        this.passwordEncoder = passwordEncoder;
    }

    // @PutMapping("/user/name")
    // public User updateName(@RequestParam String name,@RequestParam String id){
        
    //     try{
    //     User u = userService.updateName(name,id);
    //     return u;
    //     }catch(Exception e){
    //         System.out.println("there is some error"+e);
    //     }
    //     return null;
    // }

    @PutMapping("/user/{field}/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserInfo(
            @RequestBody Map<String, String> requestBody,
            @PathVariable String field,
            @PathVariable String id) {
        try {
            Long userId = Long.parseLong(id);

            // Handle password field separately because it requires multiple inputs
            if ("password".equals(field)) {
                String oldPassword = requestBody.get("oldPassword");
                String newPassword = requestBody.get("newPassword");

                if (oldPassword == null || oldPassword.trim().isEmpty() ||
                    newPassword == null || newPassword.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("Validation Error", "Old and new passwords cannot be empty."));
                }
                // Client-side typically handles confirm-password, but you can re-check here if needed
                // String confirmPassword = requestBody.get("confirmPassword");
                // if (!newPassword.equals(confirmPassword)) { ... }

                userService.updatePassword(oldPassword, newPassword, userId);
                // For password change, we usually don't return the UserDTO with password.
                // Just a success message is sufficient, or re-fetch partial user info.
                return ResponseEntity.ok(ApiResponse.success("User password updated successfully!", null)); // Or an empty DTO if preferred
            }

            // For other fields (name, email, phone)
            String newValue = requestBody.get(field);

            // Basic null/empty check for non-password fields
            if (newValue == null || newValue.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Validation Error",
                        field.substring(0, 1).toUpperCase() + field.substring(1) + " cannot be empty."));
            }

            // Call the appropriate service method based on the 'field'
            switch (field) {
                case "name":
                    userService.updateName(newValue, userId);
                    break;
                case "email":
                    userService.updateEmail(newValue, userId);
                    break;
                case "phone":
                    userService.updatePhoneNumber(newValue, userId);
                    break;
                default:
                    return ResponseEntity.badRequest().body(ApiResponse.error("Invalid Field", "Unsupported field for update: " + field));
            }

            // Fetch the updated user data to return in the response for non-password fields
            Optional<UserResponseDTO> updatedUserOptional = userService.getUserById(userId);

            if (updatedUserOptional.isPresent()) {
                UserResponseDTO updatedUserDTO = updatedUserOptional.get();
                return ResponseEntity.ok(ApiResponse.success("User " + field + " updated successfully!", updatedUserDTO));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Update failed", "User not found after update (ID: " + id + ")"));
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid Request", "User ID must be a valid number."));
        } catch (IllegalArgumentException e) {
            // Catch specific validation errors from the service layer (e.g., incorrect password, invalid format, already in use)
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation Error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during user " + field + " update: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("User " + field + " update failed", "An internal server error occurred."));
        }
    }

    @PostMapping("/user/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            UserResponseDTO newUser = userService.registerUser(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User registered successfully", newUser));
        } catch (RuntimeException e) { // Catch specific exceptions for better error messages
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("User registration failed", e.getMessage()));
        }
    }

    @PostMapping("/driver/register")
    public ResponseEntity<ApiResponse<DriverResponseDTO>> registerDriver(@Valid @RequestBody DriverRegistrationDTO registrationDTO) {
        try {
            DriverResponseDTO newDriver = driverService.registerDriver(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Driver registered successfully", newDriver));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Driver registration failed", e.getMessage()));
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<ApiResponse<UserResponseDTO>> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        return (ResponseEntity<ApiResponse<UserResponseDTO>>) userService.findUserByEmail(loginDTO.getEmail())
            .map(user -> {
                if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                    // Create UserResponseDTO from the authenticated User entity
                    UserResponseDTO userResponse = new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getRating()
                        // If you had a JWT token, you'd add it to UserResponseDTO and return it here
                        // user.getJwtToken()
                    );
                    return ResponseEntity.ok(ApiResponse.success("User logged in successfully", userResponse));
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Login failed", "Invalid credentials"));
                }
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Login failed", "Invalid credentials")));
    }

    @PostMapping("/driver/login")
    public ResponseEntity<ApiResponse<DriverResponseDTO>> loginDriver(@Valid @RequestBody LoginDTO loginDTO) {
        return (ResponseEntity<ApiResponse<DriverResponseDTO>>) driverService.findDriverByEmail(loginDTO.getEmail())
            .map(driver -> {
                if (passwordEncoder.matches(loginDTO.getPassword(), driver.getPassword())) {
                    // Create DriverResponseDTO from the authenticated Driver entity
                    DriverResponseDTO driverResponse = new DriverResponseDTO(
                        driver.getId(),
                        driver.getName(),
                        driver.getEmail(),
                        driver.getPhoneNumber(),
                        driver.getLicenseNumber(),
                        driver.getStatus(),
                        driver.getRating()
                        // If you had a JWT token, you'd add it to DriverResponseDTO and return it here
                        // driver.getJwtToken()
                    );
                    return ResponseEntity.ok(ApiResponse.success("Driver logged in successfully", driverResponse));
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Login failed", "Invalid credentials"));
                }
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Login failed", "Invalid credentials")));
    }
}
