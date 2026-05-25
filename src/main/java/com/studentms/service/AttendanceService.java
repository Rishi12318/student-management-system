package com.studentms.service;

import com.studentms.dto.request.AttendanceRequest;
import com.studentms.dto.response.AttendanceResponse;
import com.studentms.dto.response.MessageResponse;
import com.studentms.dto.response.StudentResponse;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceResponse markAttendance(AttendanceRequest request);
    AttendanceResponse updateAttendance(Long id, AttendanceRequest request);
    List<AttendanceResponse> getAttendanceByStudentAndCourse(Long studentId, Long courseId);
    Double calculateAttendancePercentage(Long studentId, Long courseId);
    List<AttendanceResponse> getAttendanceReport(Long courseId, LocalDate startDate, LocalDate endDate);
    List<StudentResponse> getLowAttendanceStudents(Long courseId, Double threshold);
}
