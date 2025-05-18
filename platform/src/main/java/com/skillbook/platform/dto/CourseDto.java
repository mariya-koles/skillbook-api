package com.skillbook.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {

    @NotBlank(message = "Title is required")
    @Schema(example = "Intro to React")
    private String title;

    @NotBlank(message = "Description is required")
    @Schema(example = "Basic introductory class to React JS")
    private String description;

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
}
