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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    // ── Student Management ───────────────────────────────────────────────────

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

    @GetMapping("/students/{id}/marks")
    @Operation(summary = "Get marks for a specific student")
    public ResponseEntity<List<MarksResponse>> getStudentMarks(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentMarks(id));
    }

    @GetMapping("/students/{id}/cgpa")
    @Operation(summary = "Calculate CGPA for a student")
    public ResponseEntity<Double> getStudentCGPA(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.calculateCGPA(id));
    }

    // ── Course Management ────────────────────────────────────────────────────

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

    @GetMapping("/courses/{id}/enrollments")
    @Operation(summary = "Get all enrollments for a course")
    public ResponseEntity<List<EnrollmentResponse>> getCourseEnrollments(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(id));
    }

    // ── Enrollment Management ────────────────────────────────────────────────

    @PostMapping("/enrollments")
    @Operation(summary = "Enroll a student in a course")
    public ResponseEntity<EnrollmentResponse> enrollStudent(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enrollStudent(request));
    }

    @PutMapping("/enrollments/{id}/status")
    @Operation(summary = "Update enrollment status")
    public ResponseEntity<EnrollmentResponse> updateEnrollmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(enrollmentService.updateEnrollmentStatus(id, status));
    }

    @DeleteMapping("/enrollments/{id}")
    @Operation(summary = "Drop an enrollment")
    public ResponseEntity<MessageResponse> dropEnrollment(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.dropEnrollment(id));
    }

    // ── Attendance Management ────────────────────────────────────────────────

    @PostMapping("/attendance")
    @Operation(summary = "Mark attendance for a student")
    public ResponseEntity<AttendanceResponse> markAttendance(@Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.markAttendance(request));
    }

    @PutMapping("/attendance/{id}")
    @Operation(summary = "Update an attendance record")
    public ResponseEntity<AttendanceResponse> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.updateAttendance(id, request));
    }

    @GetMapping("/attendance/course/{courseId}")
    @Operation(summary = "Get attendance report for a course")
    public ResponseEntity<List<AttendanceResponse>> getCourseAttendance(
            @PathVariable Long courseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(attendanceService.getAttendanceReport(courseId, startDate, endDate));
    }

    @GetMapping("/attendance/low")
    @Operation(summary = "Get students with low attendance in a course")
    public ResponseEntity<List<StudentResponse>> getLowAttendanceStudents(
            @RequestParam Long courseId,
            @RequestParam(defaultValue = "75.0") Double threshold) {
        return ResponseEntity.ok(attendanceService.getLowAttendanceStudents(courseId, threshold));
    }

    @GetMapping("/attendance/student/{studentId}/course/{courseId}")
    @Operation(summary = "Get attendance percentage for a student in a course")
    public ResponseEntity<Double> getAttendancePercentage(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        return ResponseEntity.ok(attendanceService.calculateAttendancePercentage(studentId, courseId));
    }

    // ── Marks Management ─────────────────────────────────────────────────────

    @PostMapping("/marks")
    @Operation(summary = "Add marks for a student")
    public ResponseEntity<MarksResponse> addMarks(@Valid @RequestBody MarksRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marksService.addMarks(request));
    }

    @PutMapping("/marks/{id}")
    @Operation(summary = "Update marks record")
    public ResponseEntity<MarksResponse> updateMarks(
            @PathVariable Long id,
            @Valid @RequestBody MarksRequest request) {
        return ResponseEntity.ok(marksService.updateMarks(id, request));
    }

    @DeleteMapping("/marks/{id}")
    @Operation(summary = "Delete a marks record")
    public ResponseEntity<MessageResponse> deleteMarks(@PathVariable Long id) {
        return ResponseEntity.ok(marksService.deleteMarks(id));
    }

    @GetMapping("/marks/course/{courseId}")
    @Operation(summary = "Get all marks for a course")
    public ResponseEntity<List<MarksResponse>> getMarksByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(marksService.getMarksByCourse(courseId));
    }

    @GetMapping("/marks/student/{studentId}")
    @Operation(summary = "Get all marks for a student")
    public ResponseEntity<List<MarksResponse>> getMarksByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(marksService.getMarksByStudent(studentId));
    }

    @GetMapping("/marks/average")
    @Operation(summary = "Get class average for a course and exam type")
    public ResponseEntity<Double> getClassAverage(
            @RequestParam Long courseId,
            @RequestParam String examType) {
        return ResponseEntity.ok(marksService.getClassAverage(courseId, examType));
    }
}
