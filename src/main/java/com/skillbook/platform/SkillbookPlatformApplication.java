package com.skillbook.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Skillbook Platform.
 * This class serves as the entry point for the Spring Boot application.
 */
@SpringBootApplication
public final class SkillbookPlatformApplication {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private SkillbookPlatformApplication() {
        // Utility class constructor
    }

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(SkillbookPlatformApplication.class, args);
    }

}
