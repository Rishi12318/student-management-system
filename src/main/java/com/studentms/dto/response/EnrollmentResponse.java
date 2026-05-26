package com.studentms.dto.response;

import com.studentms.model.EnrollmentStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
public class EnrollmentResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private String enrollmentNumber;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus status;
    private String grade;
    private Double obtainedMarks;
    private Double totalMarks;
}
