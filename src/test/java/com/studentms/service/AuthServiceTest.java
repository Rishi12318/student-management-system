package com.studentms.service;

import com.studentms.dto.request.SignupRequest;
import com.studentms.dto.request.LoginRequest;
import com.studentms.dto.response.JwtResponse;
import com.studentms.dto.response.MessageResponse;
import com.studentms.exception.UserAlreadyExistsException;
import com.studentms.model.*;
import com.studentms.repository.*;
import com.studentms.security.JwtUtils;
import com.studentms.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock StudentRepository studentRepository;
    @Mock AdminRepository adminRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtUtils jwtUtils;

    @InjectMocks AuthServiceImpl authService;

    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setUsername("john_doe");
        signupRequest.setEmail("john@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
    }

    @Test
    void testRegisterStudent_Success() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(studentRepository.getTotalStudentCount()).thenReturn(0L);
        when(studentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MessageResponse response = authService.registerStudent(signupRequest);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).contains("Student registered successfully");
        verify(userRepository).save(any(User.class));
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void testRegisterStudent_DuplicateUsername() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        assertThatThrownBy(() -> authService.registerStudent(signupRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Username");
    }

    @Test
    void testRegisterStudent_DuplicateEmail() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registerStudent(signupRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Email");
    }

    @Test
    void testAuthenticateUser_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("john_doe");
        loginRequest.setPassword("password123");

        User user = User.builder()
                .id(1L).username("john_doe").email("john@example.com")
                .role(Role.STUDENT).enabled(true).build();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtUtils.generateJwtToken(auth)).thenReturn("mock.jwt.token");

        JwtResponse response = authService.authenticateUser(loginRequest);

        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getUsername()).isEqualTo("john_doe");
        assertThat(response.getRole()).isEqualTo(Role.STUDENT);
    }

    @Test
    void testAuthenticateUser_InvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wrong_user");
        loginRequest.setPassword("wrong_pass");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> authService.authenticateUser(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void testValidateToken_Valid() {
        when(jwtUtils.validateJwtToken("valid.token")).thenReturn(true);
        assertThat(authService.validateToken("valid.token")).isTrue();
    }

    @Test
    void testValidateToken_Invalid() {
        when(jwtUtils.validateJwtToken("bad.token")).thenReturn(false);
        assertThat(authService.validateToken("bad.token")).isFalse();
    }
}
