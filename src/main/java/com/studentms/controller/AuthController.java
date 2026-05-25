package com.studentms.controller;

import com.studentms.dto.request.LoginRequest;
import com.studentms.dto.request.SignupRequest;
import com.studentms.dto.response.JwtResponse;
import com.studentms.dto.response.MessageResponse;
import com.studentms.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for registration and login")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup/student")
    @Operation(summary = "Register a new student")
    public ResponseEntity<MessageResponse> registerStudent(@Valid @RequestBody SignupRequest request) {
        request.setRole(com.studentms.model.Role.STUDENT);
        MessageResponse response = authService.registerStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/signup/admin")
    @Operation(summary = "Register a new admin (requires ADMIN role)")
    public ResponseEntity<MessageResponse> registerAdmin(@Valid @RequestBody SignupRequest request) {
        request.setRole(com.studentms.model.Role.ADMIN);
        MessageResponse response = authService.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT token")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest request) {
        JwtResponse jwtResponse = authService.authenticateUser(request);
        return ResponseEntity.ok(jwtResponse);
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate a JWT token")
    public ResponseEntity<MessageResponse> validateToken(@RequestParam String token) {
        boolean valid = authService.validateToken(token);
        return ResponseEntity.ok(MessageResponse.builder()
                .message(valid ? "Token is valid" : "Token is invalid or expired")
                .success(valid)
                .status(valid ? 200 : 401)
                .build());
    }
}
