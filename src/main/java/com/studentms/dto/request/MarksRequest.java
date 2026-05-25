package com.studentms.dto.request;

import com.studentms.model.ExamType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class MarksRequest {
    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Exam type is required")
    private ExamType examType;

    @NotNull(message = "Marks obtained is required")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private Double marksObtained;

    @NotNull(message = "Max marks is required")
    @DecimalMin(value = "0.0")
    private Double maxMarks;

    private LocalDate examDate;

    private String remarks;
}
