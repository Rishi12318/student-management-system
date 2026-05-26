package com.studentms.dto.response;

import com.studentms.model.ExamType;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MarksResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private String enrollmentNumber;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private ExamType examType;
    private Double marksObtained;
    private Double maxMarks;
    private Double percentage;
    private String grade;
    private LocalDate examDate;
    private String remarks;
}
}
