package com.studentms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentms.dto.request.LoginRequest;
import com.studentms.dto.request.SignupRequest;
import com.studentms.dto.response.JwtResponse;
import com.studentms.dto.response.MessageResponse;
import com.studentms.model.Role;
import com.studentms.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean AuthService authService;
    @MockBean com.studentms.security.UserDetailsServiceImpl userDetailsService;
    @MockBean com.studentms.security.JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockBean com.studentms.security.JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean com.studentms.security.JwtUtils jwtUtils;

    @Test
    void testRegisterStudent_Returns201() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFirstName("Test");
        request.setLastName("User");

        when(authService.registerStudent(any())).thenReturn(
                MessageResponse.builder().message("Student registered successfully!").success(true).status(201).build());

        mockMvc.perform(post("/api/auth/signup/student")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Student registered successfully!"));
    }

    @Test
    void testLogin_ReturnsJwt() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(authService.authenticateUser(any())).thenReturn(
                JwtResponse.builder()
                        .token("mock.jwt.token")
                        .username("testuser")
                        .email("test@example.com")
                        .role(Role.STUDENT)
                        .id(1L)
                        .build());

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testRegisterStudent_InvalidInput_Returns400() throws Exception {
        SignupRequest request = new SignupRequest();
        // missing username, email, password

        mockMvc.perform(post("/api/auth/signup/student")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testValidateToken_ValidToken() throws Exception {
        when(authService.validateToken("good.token")).thenReturn(true);

        mockMvc.perform(get("/api/auth/validate").param("token", "good.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
