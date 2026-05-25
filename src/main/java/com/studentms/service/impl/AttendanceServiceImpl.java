package com.studentms.service.impl;

import com.studentms.dto.request.AttendanceRequest;
import com.studentms.dto.response.AttendanceResponse;
import com.studentms.dto.response.MessageResponse;
import com.studentms.dto.response.StudentResponse;
import com.studentms.exception.CourseNotFoundException;
import com.studentms.exception.StudentNotFoundException;
import com.studentms.model.*;
import com.studentms.repository.*;
import com.studentms.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentServiceImpl studentService;

    @Override
    @Transactional
    public AttendanceResponse markAttendance(AttendanceRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException(request.getStudentId()));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(request.getCourseId()));

        Attendance attendance = Attendance.builder()
                .student(student)
                .course(course)
                .date(request.getDate())
                .status(request.getStatus())
                .remarks(request.getRemarks())
                .build();
        Attendance saved = attendanceRepository.save(attendance);
        log.info("Marked attendance for student {} course {}", student.getEnrollmentNumber(), course.getCourseCode());
        return mapToResponse(saved, calculateAttendancePercentage(student.getId(), course.getId()));
    }

    @Override
    @Transactional
    public AttendanceResponse updateAttendance(Long id, AttendanceRequest request) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance record not found: " + id));
        attendance.setStatus(request.getStatus());
        attendance.setRemarks(request.getRemarks());
        if (request.getDate() != null) attendance.setDate(request.getDate());
        Attendance saved = attendanceRepository.save(attendance);
        double pct = calculateAttendancePercentage(saved.getStudent().getId(), saved.getCourse().getId());
        return mapToResponse(saved, pct);
    }

    @Override
    public List<AttendanceResponse> getAttendanceByStudentAndCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        double pct = calculateAttendancePercentage(studentId, courseId);
        return attendanceRepository.findByStudentAndCourse(student, course)
                .stream().map(a -> mapToResponse(a, pct)).collect(Collectors.toList());
    }

    @Override
    public Double calculateAttendancePercentage(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        long total = attendanceRepository.countTotalDays(student, course);
        if (total == 0) return 0.0;
        long present = attendanceRepository.countPresentDays(student, course);
        return Math.round((present * 100.0 / total) * 100.0) / 100.0;
    }

    @Override
    public List<AttendanceResponse> getAttendanceReport(Long courseId, LocalDate startDate, LocalDate endDate) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        return attendanceRepository.findByCourseAndDateBetween(course, startDate, endDate)
                .stream().map(a -> {
                    double pct = calculateAttendancePercentage(a.getStudent().getId(), courseId);
                    return mapToResponse(a, pct);
                }).collect(Collectors.toList());
    }

    @Override
    public List<StudentResponse> getLowAttendanceStudents(Long courseId, Double threshold) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        return studentRepository.findAll().stream()
                .filter(s -> {
                    double pct = calculateAttendancePercentage(s.getId(), courseId);
                    return pct < threshold;
                })
                .map(studentService::mapToStudentResponse)
                .collect(Collectors.toList());
    }

    private AttendanceResponse mapToResponse(Attendance a, double pct) {
        long total = attendanceRepository.countTotalDays(a.getStudent(), a.getCourse());
        long present = attendanceRepository.countPresentDays(a.getStudent(), a.getCourse());
        return AttendanceResponse.builder()
                .id(a.getId())
                .studentId(a.getStudent().getId())
                .studentName(a.getStudent().getFullName())
                .courseId(a.getCourse().getId())
                .courseName(a.getCourse().getCourseName())
                .date(a.getDate())
                .status(a.getStatus())
                .remarks(a.getRemarks())
                .recordedBy(a.getRecordedBy())
                .recordedAt(a.getRecordedAt())
                .attendancePercentage(pct)
                .totalClasses(total)
                .presentClasses(present)
                .build();
    }
}
