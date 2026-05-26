package com.studentms.controller;

import com.studentms.dto.request.EnrollmentRequest;
import com.studentms.dto.request.StudentUpdateRequest;
import com.studentms.dto.response.*;
import com.studentms.model.User;
import com.studentms.service.EnrollmentService;
import com.studentms.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
@Tag(name = "Student", description = "Student self-service endpoints")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final StudentService studentService;
    private final EnrollmentService enrollmentService;

    @GetMapping("/profile")
    @Operation(summary = "Get logged-in student's profile")
    public ResponseEntity<StudentResponse> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(studentService.getStudentProfile(user.getId()));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update student profile")
    public ResponseEntity<StudentResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StudentUpdateRequest request) {
        StudentResponse profile = studentService.getStudentProfile(user.getId());
        return ResponseEntity.ok(studentService.updateStudentProfile(profile.getId(), request));
    }

    @PostMapping("/enroll")
    @Operation(summary = "Enroll in a course")
    public ResponseEntity<EnrollmentResponse> enrollCourse(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enrollStudent(request));
    }

    @GetMapping("/cgpa")
    @Operation(summary = "Calculate CGPA for logged-in student")
    public ResponseEntity<Double> getCGPA(@AuthenticationPrincipal User user) {
        StudentResponse profile = studentService.getStudentProfile(user.getId());
        return ResponseEntity.ok(studentService.calculateCGPA(profile.getId()));
    }
}
