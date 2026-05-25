package com.studentms.dto.response;

import com.studentms.model.AttendanceStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private LocalDate date;
    private AttendanceStatus status;
    private String remarks;
    private String recordedBy;
    private LocalDateTime recordedAt;
    private Double attendancePercentage;
    private Long totalClasses;
    private Long presentClasses;
}
