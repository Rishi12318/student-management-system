package com.studentms.dto.response;

import com.studentms.model.AttendanceStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AttendanceResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private LocalDate date;
    private AttendanceStatus status;
    private String remarks;
    private String recordedBy;
    private LocalDateTime recordedAt;
    private Double attendancePercentage;
}
