package com.studentms.service;

import com.studentms.dto.request.LoginRequest;
import com.studentms.dto.request.SignupRequest;
import com.studentms.dto.response.JwtResponse;
import com.studentms.dto.response.MessageResponse;

public interface AuthService {
    MessageResponse registerStudent(SignupRequest request);
    MessageResponse registerAdmin(SignupRequest request);
    JwtResponse authenticateUser(LoginRequest request);
    boolean validateToken(String token);
}
