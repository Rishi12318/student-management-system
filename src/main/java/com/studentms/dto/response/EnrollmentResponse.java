package com.studentms.dto.response;

import com.studentms.model.EnrollmentStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long id;
    private StudentResponse student;
    private CourseResponse course;
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus status;
    private String grade;
    private Double obtainedMarks;
    private Double totalMarks;
}
