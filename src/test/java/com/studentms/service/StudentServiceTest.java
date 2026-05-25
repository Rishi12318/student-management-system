package com.studentms.service;

import com.studentms.dto.request.StudentUpdateRequest;
import com.studentms.dto.response.MarksResponse;
import com.studentms.dto.response.StudentResponse;
import com.studentms.exception.StudentNotFoundException;
import com.studentms.model.*;
import com.studentms.repository.*;
import com.studentms.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock StudentRepository studentRepository;
    @Mock EnrollmentRepository enrollmentRepository;
    @Mock AttendanceRepository attendanceRepository;
    @Mock MarksRepository marksRepository;

    @InjectMocks StudentServiceImpl studentService;

    private Student student;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("jane_doe").email("jane@example.com")
                .role(Role.STUDENT).enabled(true).build();
        student = Student.builder()
                .id(10L).enrollmentNumber("STU20260001")
                .firstName("Jane").lastName("Doe")
                .dateOfBirth(LocalDate.of(2000, 1, 15))
                .phoneNumber("9876543210")
                .gender(Gender.FEMALE)
                .user(user)
                .build();
    }

    @Test
    void testGetStudentProfile_Success() {
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(student));
        StudentResponse response = studentService.getStudentProfile(1L);

        assertThat(response.getEnrollmentNumber()).isEqualTo("STU20260001");
        assertThat(response.getFullName()).isEqualTo("Jane Doe");
    }

    @Test
    void testGetStudentProfile_NotFound() {
        when(studentRepository.findByUserId(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> studentService.getStudentProfile(99L))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    void testUpdateStudentProfile_Success() {
        StudentUpdateRequest request = new StudentUpdateRequest();
        request.setPhoneNumber("1234567890");
        request.setAddress("New Address, Mumbai");

        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StudentResponse response = studentService.updateStudentProfile(10L, request);

        assertThat(response.getPhoneNumber()).isEqualTo("1234567890");
        assertThat(response.getAddress()).isEqualTo("New Address, Mumbai");
    }

    @Test
    void testUpdateStudentProfile_StudentNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> studentService.updateStudentProfile(99L, new StudentUpdateRequest()))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    void testGetAllStudents_Success() {
        when(studentRepository.findAllByOrderByEnrollmentNumber()).thenReturn(List.of(student));
        List<StudentResponse> students = studentService.getAllStudents();
        assertThat(students).hasSize(1);
        assertThat(students.get(0).getEnrollmentNumber()).isEqualTo("STU20260001");
    }

    @Test
    void testDeleteStudent_Success() {
        when(studentRepository.existsById(10L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(10L);
        var response = studentService.deleteStudent(10L);
        assertThat(response.isSuccess()).isTrue();
        verify(studentRepository).deleteById(10L);
    }

    @Test
    void testDeleteStudent_NotFound() {
        when(studentRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> studentService.deleteStudent(99L))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    void testCalculateCGPA_WithMarks() {
        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        when(marksRepository.getOverallPercentageForStudent(student)).thenReturn(85.0);

        Double cgpa = studentService.calculateCGPA(10L);
        assertThat(cgpa).isEqualTo(8.5);
    }

    @Test
    void testCalculateCGPA_NoMarks() {
        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        when(marksRepository.getOverallPercentageForStudent(student)).thenReturn(null);

        Double cgpa = studentService.calculateCGPA(10L);
        assertThat(cgpa).isEqualTo(0.0);
    }
}
