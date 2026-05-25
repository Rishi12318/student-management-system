package com.studentms.service.impl;

import com.studentms.dto.request.EnrollmentRequest;
import com.studentms.dto.response.CourseResponse;
import com.studentms.dto.response.EnrollmentResponse;
import com.studentms.dto.response.MessageResponse;
import com.studentms.dto.response.StudentResponse;
import com.studentms.exception.*;
import com.studentms.model.*;
import com.studentms.repository.*;
import com.studentms.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentServiceImpl studentService;
    private final CourseServiceImpl courseService;

    @Override
    @Transactional
    public EnrollmentResponse enrollStudent(EnrollmentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException(request.getStudentId()));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(request.getCourseId()));

        if (enrollmentRepository.existsByStudentAndCourseAndStatus(student, course, EnrollmentStatus.ACTIVE)) {
            throw new DuplicateEnrollmentException("Student is already enrolled in this course");
        }
        if (!course.getIsActive()) {
            throw new IllegalArgumentException("Cannot enroll in an inactive course");
        }
        long currentCount = enrollmentRepository.countActiveEnrollmentsByCourse(course);
        if (currentCount >= course.getMaxSeats()) {
            throw new IllegalArgumentException("Course is full. No seats available");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .status(EnrollmentStatus.ACTIVE)
                .build();
        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("Enrolled student {} in course {}", student.getEnrollmentNumber(), course.getCourseCode());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public MessageResponse dropEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException(enrollmentId));
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
        log.info("Dropped enrollment: {}", enrollmentId);
        return MessageResponse.builder().message("Enrollment dropped successfully").success(true).status(200).build();
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        return enrollmentRepository.findByStudent(student).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        return enrollmentRepository.findByCourse(course).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EnrollmentResponse updateEnrollmentStatus(Long enrollmentId, String status) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException(enrollmentId));
        enrollment.setStatus(EnrollmentStatus.valueOf(status.toUpperCase()));
        return mapToResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    public boolean checkEnrollmentEligibility(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        if (!course.getIsActive()) return false;
        if (enrollmentRepository.existsByStudentAndCourseAndStatus(student, course, EnrollmentStatus.ACTIVE)) return false;
        long currentCount = enrollmentRepository.countActiveEnrollmentsByCourse(course);
        return currentCount < course.getMaxSeats();
    }

    private EnrollmentResponse mapToResponse(Enrollment e) {
        StudentResponse sr = studentService.mapToStudentResponse(e.getStudent());
        CourseResponse cr = courseService.mapToResponse(e.getCourse());
        return EnrollmentResponse.builder()
                .id(e.getId())
                .student(sr)
                .course(cr)
                .enrollmentDate(e.getEnrollmentDate())
                .status(e.getStatus())
                .grade(e.getGrade())
                .obtainedMarks(e.getObtainedMarks())
                .totalMarks(e.getTotalMarks())
                .build();
    }
}
