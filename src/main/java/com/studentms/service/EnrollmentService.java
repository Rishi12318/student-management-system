package com.studentms.service;

import com.studentms.dto.request.EnrollmentRequest;
import com.studentms.dto.response.EnrollmentResponse;

public interface EnrollmentService {
    EnrollmentResponse enrollStudent(EnrollmentRequest request);
}