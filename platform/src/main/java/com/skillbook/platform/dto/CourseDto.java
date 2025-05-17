package com.skillbook.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {

    @Schema(example = "Intro to React")
    private String title;

    @Schema(example = "Basic introductory class to React JS")
    private String description;

    @Schema(example = "Programming")
    private String category;

    @Schema(example = "1", description = "Existing instructor's ID in the system")
    private Long instructorId;

    @Schema(example = "2025-05-15T16:54:14.628Z")
    private LocalDateTime startTime;

    @Schema(example = "90")
    private int durationMinutes;
}
