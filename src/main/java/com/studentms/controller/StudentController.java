package com.studentms.controller;

import com.studentms.dto.request.EnrollmentRequest;
import com.studentms.dto.request.StudentUpdateRequest;
import com.studentms.dto.response.*;
import com.studentms.model.User;
import com.studentms.service.AttendanceService;
import com.studentms.service.EnrollmentService;
import com.studentms.service.MarksService;
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

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
@Tag(name = "Student", description = "Student self-service endpoints")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    private final AttendanceService attendanceService;
    private final MarksService marksService;

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

    @GetMapping("/enrollments")
    @Operation(summary = "Get all enrollments for logged-in student")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(@AuthenticationPrincipal User user) {
        StudentResponse profile = studentService.getStudentProfile(user.getId());
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(profile.getId()));
    }

    @PostMapping("/enroll")
    @Operation(summary = "Enroll in a course")
    public ResponseEntity<EnrollmentResponse> enrollCourse(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enrollStudent(request));
    }

    @DeleteMapping("/enroll/{enrollmentId}")
    @Operation(summary = "Drop a course enrollment")
    public ResponseEntity<MessageResponse> dropEnrollment(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(enrollmentService.dropEnrollment(enrollmentId));
    }

    @GetMapping("/attendance/{courseId}")
    @Operation(summary = "Get attendance for a specific course")
    public ResponseEntity<List<AttendanceResponse>> getAttendance(
            @AuthenticationPrincipal User user,
            @PathVariable Long courseId) {
        StudentResponse profile = studentService.getStudentProfile(user.getId());
        return ResponseEntity.ok(attendanceService.getAttendanceByStudentAndCourse(profile.getId(), courseId));
    }

    @GetMapping("/attendance/{courseId}/percentage")
    @Operation(summary = "Get attendance percentage for a course")
    public ResponseEntity<Double> getAttendancePercentage(
            @AuthenticationPrincipal User user,
            @PathVariable Long courseId) {
        StudentResponse profile = studentService.getStudentProfile(user.getId());
        return ResponseEntity.ok(attendanceService.calculateAttendancePercentage(profile.getId(), courseId));
    }

    @GetMapping("/marks")
    @Operation(summary = "Get all marks for logged-in student")
    public ResponseEntity<List<MarksResponse>> getMyMarks(@AuthenticationPrincipal User user) {
        StudentResponse profile = studentService.getStudentProfile(user.getId());
        return ResponseEntity.ok(marksService.getMarksByStudent(profile.getId()));
    }

    @GetMapping("/cgpa")
    @Operation(summary = "Calculate CGPA for logged-in student")
    public ResponseEntity<Double> getCGPA(@AuthenticationPrincipal User user) {
        StudentResponse profile = studentService.getStudentProfile(user.getId());
        return ResponseEntity.ok(studentService.calculateCGPA(profile.getId()));
    }
}
