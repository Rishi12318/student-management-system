package com.studentms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CourseRequest {
    @NotBlank(message = "Course code is required")
    @Size(min = 3, max = 20)
    private String courseCode;

    @NotBlank(message = "Course name is required")
    @Size(min = 3, max = 100)
    private String courseName;

    @Min(1) @Max(6)
    private Integer credits;

    @Min(1) @Max(8)
    private Integer semester;

    @Size(max = 500)
    private String description;

    private String department;

    private Boolean isActive = true;

    @Min(10) @Max(100)
    private Integer maxSeats;
}
