package com.skillbook.platform.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbook.platform.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "users", schema="public")  // "user" is a reserved keyword in PostgreSQL
@Schema(description = "User model used for login and registration")
public class User {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(example = "john_doe")
    @Column(name = "username")
    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(example = "securePassword123")
    @NotBlank(message = "Password is required")
    private String password;

    @Schema(example = "john")
    @Column(name = "firstname")
    private String firstName;

    @Schema(example = "doe")
    @Column(name = "lastname")
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role; // e.g. LEARNER, INSTRUCTOR, ADMIN

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "profilepic", columnDefinition = "bytea")
    @ToString.Exclude
    private byte[] profilepic;

    @ManyToMany
    @JoinTable(
            name = "user_course_enrollments",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @ToString.Exclude
    private Set<Course> enrolledCourses = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
