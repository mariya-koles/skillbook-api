package com.skillbook.platform.repository;

import com.skillbook.platform.model.User;
import com.skillbook.platform.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByUsername_thenReturnUser() {
        // given
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role(Role.LEARNER)
                .build();
        
        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByUsername(user.getUsername()).orElse(null);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo(user.getUsername());
        assertThat(found.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void whenInvalidUsername_thenReturnNull() {
        // when
        User fromDb = userRepository.findByUsername("nonexistentuser").orElse(null);

        // then
        assertThat(fromDb).isNull();
    }

    @Test
    public void whenSaveUser_thenReturnSavedUser() {
        // given
        User user = User.builder()
                .username("newuser")
                .email("new@example.com")
                .password("newpass123")
                .role(Role.INSTRUCTOR)
                .build();

        // when
        User saved = userRepository.save(user);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo(user.getUsername());
    }
} 