package com.studentms.service.impl;

import com.studentms.dto.request.AttendanceRequest;
import com.studentms.dto.response.AttendanceResponse;
import com.studentms.exception.ResourceNotFoundException;
import com.studentms.model.Attendance;
import com.studentms.model.Course;
import com.studentms.model.Student;
import com.studentms.repository.AttendanceRepository;
import com.studentms.repository.CourseRepository;
import com.studentms.repository.StudentRepository;
import com.studentms.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    
    @Override
    public AttendanceResponse markAttendance(AttendanceRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        Attendance attendance = Attendance.builder()
                .student(student)
                .course(course)
                .date(request.getDate())
                .status(request.getStatus())
                .remarks(request.getRemarks())
                .build();
        
        attendance = attendanceRepository.save(attendance);
        
        return mapToAttendanceResponse(attendance);
    }
    
    private AttendanceResponse mapToAttendanceResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .studentId(attendance.getStudent().getId())
                .studentName(attendance.getStudent().getFullName())
                .courseId(attendance.getCourse().getId())
                .courseCode(attendance.getCourse().getCourseCode())
                .courseName(attendance.getCourse().getCourseName())
                .date(attendance.getDate())
                .status(attendance.getStatus())
                .remarks(attendance.getRemarks())
                .recordedBy(attendance.getRecordedBy())
                .recordedAt(attendance.getRecordedAt())
                .attendancePercentage(0.0)
                .build();
    }
}