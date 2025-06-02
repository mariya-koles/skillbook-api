package com.skillbook.platform.security;

import com.skillbook.platform.controller.CourseController;
import com.skillbook.platform.model.User;
import com.skillbook.platform.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(CourseController.class);

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Looking for: " + username);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.info("User not found.");
                return new UsernameNotFoundException("User not found");
            });

        log.info("Login successful for user ID: {}", user.getId());


        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
