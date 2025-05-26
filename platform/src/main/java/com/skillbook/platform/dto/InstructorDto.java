package com.skillbook.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}