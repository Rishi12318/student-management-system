package com.studentms.service.impl;

import com.studentms.dto.request.StudentUpdateRequest;
import com.studentms.dto.response.*;
import com.studentms.exception.StudentNotFoundException;
import com.studentms.model.*;
import com.studentms.repository.MarksRepository;
import com.studentms.repository.StudentRepository;
import com.studentms.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {
    
    private final StudentRepository studentRepository;
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
        
        student = studentRepository.save(student);
        log.info("Student profile updated successfully for id: {}", studentId);
        
        return mapToStudentResponse(student);
    }
    
    @Override
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAllByOrderByEnrollmentNumber().stream()
                .map(this::mapToStudentResponse)
                .collect(Collectors.toList());
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
        
        log.info("Student deleted successfully with id: {}", id);
        
        return MessageResponse.builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Student deleted successfully!")
                .build();
    }
    
    @Override
    public Double calculateCGPA(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        Double overallPercentage = marksRepository.getOverallPercentageForStudent(student);
        if (overallPercentage == null) {
            return 0.0;
        }
        return Math.round((overallPercentage / 10.0) * 10.0) / 10.0;
    }
    
    private StudentResponse mapToStudentResponse(Student student) {
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
                .updatedAt(student.getUpdatedAt())
                .build();
    }
}