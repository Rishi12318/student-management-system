package com.studentms.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CourseResponse {
    private Long id;
    private String courseCode;
    private String courseName;
    private Integer credits;
    private Integer semester;
    private String description;
    private String department;
    private Boolean isActive;
    private Integer maxSeats;
    private Long enrolledStudentsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
