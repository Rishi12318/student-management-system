package com.studentms.controller;

import com.studentms.dto.request.AttendanceRequest;
import com.studentms.dto.request.CourseRequest;
import com.studentms.dto.request.EnrollmentRequest;
import com.studentms.dto.request.MarksRequest;
import com.studentms.dto.request.StudentUpdateRequest;
import com.studentms.dto.response.*;
import com.studentms.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final AttendanceService attendanceService;
    private final MarksService marksService;

    // â”€â”€ Student Management â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @GetMapping("/students")
    @Operation(summary = "Get all students")
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/students/{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @PutMapping("/students/{id}")
    @Operation(summary = "Update student details")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentUpdateRequest request) {
        return ResponseEntity.ok(studentService.updateStudentProfile(id, request));
    }

    @DeleteMapping("/students/{id}")
    @Operation(summary = "Delete a student")
    public ResponseEntity<MessageResponse> deleteStudent(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.deleteStudent(id));
    }

    @GetMapping("/students/{id}/cgpa")
    @Operation(summary = "Calculate CGPA for a student")
    public ResponseEntity<Double> getStudentCGPA(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.calculateCGPA(id));
    }

    // â”€â”€ Course Management â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @PostMapping("/courses")
    @Operation(summary = "Create a new course")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(request));
    }

    @GetMapping("/courses")
    @Operation(summary = "Get all courses")
    public ResponseEntity<List<CourseResponse>> getAllCourses(
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) String keyword) {
        if (keyword != null) return ResponseEntity.ok(courseService.searchCourses(keyword));
        if (semester != null) return ResponseEntity.ok(courseService.getCoursesBySemester(semester));
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/courses/{id}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PutMapping("/courses/{id}")
    @Operation(summary = "Update course details")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @DeleteMapping("/courses/{id}")
    @Operation(summary = "Delete a course")
    public ResponseEntity<MessageResponse> deleteCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.deleteCourse(id));
    }

    // â”€â”€ Enrollment Management â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @PostMapping("/enrollments")
    @Operation(summary = "Enroll a student in a course")
    public ResponseEntity<EnrollmentResponse> enrollStudent(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enrollStudent(request));
    }

    // â”€â”€ Attendance Management â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @PostMapping("/attendance")
    @Operation(summary = "Mark attendance for a student")
    public ResponseEntity<AttendanceResponse> markAttendance(@Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.markAttendance(request));
    }

    // â”€â”€ Marks Management â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @PostMapping("/marks")
    @Operation(summary = "Add marks for a student")
    public ResponseEntity<MarksResponse> addMarks(@Valid @RequestBody MarksRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marksService.addMarks(request));
    }
}
