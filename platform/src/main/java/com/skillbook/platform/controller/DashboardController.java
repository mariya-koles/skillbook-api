package com.skillbook.platform.controller;

import com.skillbook.platform.dto.UserDto;
import com.skillbook.platform.model.User;
import com.skillbook.platform.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Controller handling user dashboard functionality.
 * Provides endpoints for creating updating user account information.
 *
 * @author mariya-koles
 * @version 1.0
 * @since 2025-03
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;


    /**
     * Updates the authenticated user's profile information.
     * Accepts multipart/form-data with a JSON part for user fields and a binary part for the profile picture.
     *
     * @param userDto JSON user details
     * @param photo optional profile photo file
     * @param principal the authenticated user's identity
     * @return ResponseEntity with update status
     * @throws Exception
     * @HTTP 200 OK if update is successful
     * @HTTP 400 Bad Request if validation fails
     */
    @PostMapping(value = "/users/update-profile", consumes = "multipart/form-data")
    public ResponseEntity<?> updateUserProfile(
            @RequestPart("user") UserDto userDto,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            Principal principal
    ) throws Exception {
        String username = principal.getName();

        // Lookup the user by username
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Update fields
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());

        if (photo != null && !photo.isEmpty()) {
            user.setProfilePhoto(photo.getBytes());
        }

        userRepository.save(user);
        return ResponseEntity.ok("User profile updated successfully");
    }
}

