package com.studentms.service;

import com.studentms.dto.request.AttendanceRequest;
import com.studentms.dto.response.AttendanceResponse;

public interface AttendanceService {
    AttendanceResponse markAttendance(AttendanceRequest request);
}