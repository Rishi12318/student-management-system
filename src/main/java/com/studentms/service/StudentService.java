package com.studentms.service;

import com.studentms.dto.request.StudentUpdateRequest;
import com.studentms.dto.response.AttendanceResponse;
import com.studentms.dto.response.EnrollmentResponse;
import com.studentms.dto.response.MarksResponse;
import com.studentms.dto.response.MessageResponse;
import com.studentms.dto.response.StudentResponse;

import java.util.List;

public interface StudentService {
    StudentResponse getStudentProfile(Long userId);
    StudentResponse updateStudentProfile(Long studentId, StudentUpdateRequest request);
    List<StudentResponse> getAllStudents();
    StudentResponse getStudentById(Long id);
    MessageResponse deleteStudent(Long id);
    List<EnrollmentResponse> getStudentEnrollments(Long studentId);
    List<AttendanceResponse> getStudentAttendance(Long studentId, Long courseId);
    List<MarksResponse> getStudentMarks(Long studentId);
    Double calculateCGPA(Long studentId);
}
