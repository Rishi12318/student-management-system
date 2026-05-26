package com.studentms.service.impl;

import com.studentms.dto.request.EnrollmentRequest;
import com.studentms.dto.response.EnrollmentResponse;
import com.studentms.exception.ResourceNotFoundException;
import com.studentms.model.Course;
import com.studentms.model.Enrollment;
import com.studentms.model.Student;
import com.studentms.repository.CourseRepository;
import com.studentms.repository.EnrollmentRepository;
import com.studentms.repository.StudentRepository;
import com.studentms.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {
    
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    
    @Override
    public EnrollmentResponse enrollStudent(EnrollmentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();
        
        enrollment = enrollmentRepository.save(enrollment);
        
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent().getId())
                .studentName(enrollment.getStudent().getFullName())
                .enrollmentNumber(enrollment.getStudent().getEnrollmentNumber())
                .courseId(enrollment.getCourse().getId())
                .courseCode(enrollment.getCourse().getCourseCode())
                .courseName(enrollment.getCourse().getCourseName())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus())
                .grade(enrollment.getGrade())
                .obtainedMarks(enrollment.getObtainedMarks())
                .totalMarks(enrollment.getTotalMarks())
                .build();
    }
}