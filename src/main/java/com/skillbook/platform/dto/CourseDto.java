package com.skillbook.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Data Transfer Object for Courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {

    @Schema(example = "1", description = "Course unique ID",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Schema(example = "Intro to React")
    private String title;

    @NotBlank(message = "Description is required")
    @Schema(example = "Basic introductory class to React JS")
    private String description;

    @Schema(example = "This introductory course to React JS is designed for" +
            " anyone who wants to start building modern, dynamic web applications using " +
            "one of the most popular JavaScript libraries in the world. Whether you're a" +
            " beginner in front-end development or looking to expand your skills beyond" +
            " HTML, CSS, and vanilla JavaScript, this course will guide you through the " +
            "essential concepts and hands-on techniques that make React such a powerful " +
            "tool for developers.")
    private String longDescription;

    @NotBlank(message = "Category is required")
    @Schema(example = "Programming")
    private String category;

    @Schema(example = "1", description = "Existing instructor's ID in the system")
    private Long instructorId;

    @NotNull(message = "Start time is required")
    @Schema(example = "2025-05-15T16:54:14.628Z")
    private LocalDateTime startTime;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Schema(example = "90")
    private Integer durationMinutes;

    private Set<Long> enrolledUserIds; // or List<UserDto>

    private InstructorDto instructor;

}
