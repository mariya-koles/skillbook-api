package com.skillbook.platform.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.skillbook.platform.enums.Role;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "users", schema="public")  // "user" is a reserved keyword in PostgreSQL
@Schema(description = "User model used for login and registration")
public class User {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(example = "john_doe")
    @Column(name = "username")
    private String username;

    private String email;

    @Schema(example = "securePassword123")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // e.g. LEARNER, INSTRUCTOR, ADMIN


}
