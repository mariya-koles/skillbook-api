package com.skillbook.platform.controller;

import com.skillbook.platform.dto.UserDto;
import com.skillbook.platform.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns the current user's full data including enrolled courses.
     *
     * @param authentication injected by Spring Security
     * @return the current user's UserDto
     */
    @GetMapping("/me")
    public UserDto getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(@RequestBody UserDto dto,
                                                     Authentication authentication) {
        String username = authentication.getName();
        UserDto existing = userService.findByUsername(username);

        dto.setId(existing.getId());

        userService.updateUser(dto);
        UserDto updatedUser = userService.findByUsername(username);
        return ResponseEntity.ok(updatedUser); // 200 OK with updated profile info
    }

}
