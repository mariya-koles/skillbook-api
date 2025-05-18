package com.skillbook.platform.dto;

import com.skillbook.platform.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Data Transfer Object for User")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    @Schema(example = "john_doe", description = "Unique username for login")
    private String username;

    @Schema(example = "securePassword123", description = "User's password (plain text for input; hashed in DB)")
    private String password;

    @Schema(example = "john.doe@example.com", description = "Email address of the user")
    private String email;

    @Schema(example = "INSTRUCTOR", description = "Role assigned to the user (e.g., ADMIN, INSTRUCTOR, LEARNER)")
    private Role role;

    @Schema(example = "John", description = "First name of the user")
    private String firstName;

    @Schema(example = "Doe", description = "Last name of the user")
    private String lastName;

    @Schema(description = "Profile picture binary data as byte array")
    private byte[] profilepic;
}
