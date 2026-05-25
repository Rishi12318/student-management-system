package com.studentms.service.impl;

import com.studentms.dto.request.LoginRequest;
import com.studentms.dto.request.SignupRequest;
import com.studentms.dto.response.JwtResponse;
import com.studentms.dto.response.MessageResponse;
import com.studentms.exception.UserAlreadyExistsException;
import com.studentms.model.*;
import com.studentms.repository.AdminRepository;
import com.studentms.repository.StudentRepository;
import com.studentms.repository.UserRepository;
import com.studentms.security.JwtUtils;
import com.studentms.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public MessageResponse registerStudent(SignupRequest request) {
        validateUniqueCredentials(request);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .enabled(true)
                .build();
        userRepository.save(user);

        String enrollmentNumber = generateEnrollmentNumber();
        Student student = Student.builder()
                .enrollmentNumber(enrollmentNumber)
                .firstName(request.getFirstName() != null ? request.getFirstName() : request.getUsername())
                .lastName(request.getLastName() != null ? request.getLastName() : "")
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .gender(request.getGender())
                .user(user)
                .build();
        studentRepository.save(student);

        log.info("Registered new student: {} with enrollment: {}", request.getUsername(), enrollmentNumber);
        return MessageResponse.builder()
                .message("Student registered successfully! Enrollment No: " + enrollmentNumber)
                .success(true)
                .status(201)
                .build();
    }

    @Override
    @Transactional
    public MessageResponse registerAdmin(SignupRequest request) {
        validateUniqueCredentials(request);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .enabled(true)
                .build();
        userRepository.save(user);

        Admin admin = Admin.builder()
                .name(request.getName() != null ? request.getName() : request.getUsername())
                .department(request.getDepartment())
                .user(user)
                .build();
        adminRepository.save(admin);

        log.info("Registered new admin: {}", request.getUsername());
        return MessageResponse.builder()
                .message("Admin registered successfully!")
                .success(true)
                .status(201)
                .build();
    }

    @Override
    public JwtResponse authenticateUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = (User) authentication.getPrincipal();
        log.info("User authenticated: {}", user.getUsername());

        return JwtResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtils.validateJwtToken(token);
    }

    private void validateUniqueCredentials(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email '" + request.getEmail() + "' is already registered");
        }
    }

    private String generateEnrollmentNumber() {
        int year = Year.now().getValue();
        long count = studentRepository.getTotalStudentCount() + 1;
        return String.format("STU%d%04d", year, count);
    }
}
