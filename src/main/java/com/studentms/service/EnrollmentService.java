package com.studentms.service;

import com.studentms.dto.request.EnrollmentRequest;
import com.studentms.dto.response.EnrollmentResponse;
import com.studentms.dto.response.MessageResponse;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse enrollStudent(EnrollmentRequest request);
    MessageResponse dropEnrollment(Long enrollmentId);
    List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId);
    List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId);
    EnrollmentResponse updateEnrollmentStatus(Long enrollmentId, String status);
    boolean checkEnrollmentEligibility(Long studentId, Long courseId);
}
