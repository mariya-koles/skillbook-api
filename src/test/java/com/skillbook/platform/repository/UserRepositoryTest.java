package com.skillbook.platform.repository;

import com.skillbook.platform.model.User;
import com.skillbook.platform.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User createTestUser(String username) {
        byte[] emptyPic = new byte[] { (byte)0 }; // Single byte array with value 0
        return User.builder()
                .username(username)
                .email(username + "@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .role(Role.LEARNER)
                .profilePhoto(emptyPic)
                .build();
    }

    @Test
    public void whenFindByUsername_thenReturnUser() {
        // given
        User user = createTestUser("testuser");
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findByUsername(user.getUsername());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(user.getUsername());
        assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(found.get().getFirstName()).isEqualTo(user.getFirstName());
        assertThat(found.get().getLastName()).isEqualTo(user.getLastName());
        assertThat(found.get().getRole()).isEqualTo(user.getRole());
        assertThat(found.get().getProfilePhoto()).isNotNull();
    }

    @Test
    public void whenInvalidUsername_thenReturnNull() {
        // when
        Optional<User> notFound = userRepository.findByUsername("nonexistent");

        // then
        assertThat(notFound).isEmpty();
    }

    @Test
    public void whenSaveUser_thenReturnSavedUser() {
        // given
        User user = createTestUser("newuser");

        // when
        User saved = userRepository.save(user);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("newuser");
        assertThat(saved.getEmail()).isEqualTo("newuser@example.com");
        assertThat(saved.getFirstName()).isEqualTo("Test");
        assertThat(saved.getLastName()).isEqualTo("User");
        assertThat(saved.getRole()).isEqualTo(Role.LEARNER);
        assertThat(saved.getProfilePhoto()).isNotNull();
    }
} 