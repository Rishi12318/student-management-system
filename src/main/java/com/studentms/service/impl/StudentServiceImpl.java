package com.studentms.service.impl;

import com.studentms.dto.request.StudentUpdateRequest;
import com.studentms.dto.response.*;
import com.studentms.exception.StudentNotFoundException;
import com.studentms.model.*;
import com.studentms.repository.*;
import com.studentms.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarksRepository marksRepository;

    @Override
    public StudentResponse getStudentProfile(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found for user id: " + userId));
        return mapToStudentResponse(student);
    }

    @Override
    @Transactional
    public StudentResponse updateStudentProfile(Long studentId, StudentUpdateRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        if (request.getFirstName() != null) student.setFirstName(request.getFirstName());
        if (request.getLastName() != null) student.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null) student.setDateOfBirth(request.getDateOfBirth());
        if (request.getPhoneNumber() != null) student.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) student.setAddress(request.getAddress());
        if (request.getGender() != null) student.setGender(request.getGender());

        Student updated = studentRepository.save(student);
        log.info("Updated student profile: {}", studentId);
        return mapToStudentResponse(updated);
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAllByOrderByEnrollmentNumber()
                .stream().map(this::mapToStudentResponse).collect(Collectors.toList());
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        return mapToStudentResponse(student);
    }

    @Override
    @Transactional
    public MessageResponse deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
        log.info("Deleted student: {}", id);
        return MessageResponse.builder().message("Student deleted successfully").success(true).status(200).build();
    }

    @Override
    public List<EnrollmentResponse> getStudentEnrollments(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        return enrollmentRepository.findByStudent(student)
                .stream().map(this::mapToEnrollmentResponse).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getStudentAttendance(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        return attendanceRepository.findByStudentAndCourse(student,
                student.getEnrollments().stream()
                        .filter(e -> e.getCourse().getId().equals(courseId))
                        .findFirst().orElseThrow().getCourse())
                .stream().map(this::mapToAttendanceResponse).collect(Collectors.toList());
    }

    @Override
    public List<MarksResponse> getStudentMarks(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        return marksRepository.findByStudent(student)
                .stream().map(this::mapToMarksResponse).collect(Collectors.toList());
    }

    @Override
    public Double calculateCGPA(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        Double percentage = marksRepository.getOverallPercentageForStudent(student);
        if (percentage == null) return 0.0;
        // Convert percentage to CGPA on 10-point scale
        return Math.round((percentage / 10.0) * 100.0) / 100.0;
    }

    public StudentResponse mapToStudentResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .enrollmentNumber(student.getEnrollmentNumber())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .fullName(student.getFullName())
                .dateOfBirth(student.getDateOfBirth())
                .phoneNumber(student.getPhoneNumber())
                .address(student.getAddress())
                .gender(student.getGender())
                .profilePicture(student.getProfilePicture())
                .username(student.getUser() != null ? student.getUser().getUsername() : null)
                .email(student.getUser() != null ? student.getUser().getEmail() : null)
                .createdAt(student.getCreatedAt())
                .build();
    }

    private EnrollmentResponse mapToEnrollmentResponse(Enrollment e) {
        return EnrollmentResponse.builder()
                .id(e.getId())
                .student(mapToStudentResponse(e.getStudent()))
                .course(mapToCourseResponse(e.getCourse()))
                .enrollmentDate(e.getEnrollmentDate())
                .status(e.getStatus())
                .grade(e.getGrade())
                .obtainedMarks(e.getObtainedMarks())
                .totalMarks(e.getTotalMarks())
                .build();
    }

    private CourseResponse mapToCourseResponse(Course c) {
        return CourseResponse.builder()
                .id(c.getId())
                .courseCode(c.getCourseCode())
                .courseName(c.getCourseName())
                .credits(c.getCredits())
                .semester(c.getSemester())
                .description(c.getDescription())
                .department(c.getDepartment())
                .isActive(c.getIsActive())
                .maxSeats(c.getMaxSeats())
                .createdAt(c.getCreatedAt())
                .build();
    }

    private AttendanceResponse mapToAttendanceResponse(Attendance a) {
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
                .build();
    }

    private MarksResponse mapToMarksResponse(Marks m) {
        double pct = m.getMaxMarks() > 0 ? (m.getMarksObtained() / m.getMaxMarks()) * 100 : 0;
        return MarksResponse.builder()
                .id(m.getId())
                .studentId(m.getStudent().getId())
                .studentName(m.getStudent().getFullName())
                .courseId(m.getCourse().getId())
                .courseName(m.getCourse().getCourseName())
                .examType(m.getExamType())
                .marksObtained(m.getMarksObtained())
                .maxMarks(m.getMaxMarks())
                .percentage(Math.round(pct * 100.0) / 100.0)
                .grade(calculateGrade(pct))
                .examDate(m.getExamDate())
                .remarks(m.getRemarks())
                .createdAt(m.getCreatedAt())
                .build();
    }

    private String calculateGrade(double pct) {
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B+";
        if (pct >= 60) return "B";
        if (pct >= 50) return "C";
        if (pct >= 40) return "D";
        return "F";
    }
}
