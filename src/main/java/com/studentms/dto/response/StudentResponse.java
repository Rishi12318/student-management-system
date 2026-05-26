package com.studentms.dto.response;

import com.studentms.model.Gender;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class StudentResponse {
    private Long id;
    private String enrollmentNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String address;
    private Gender gender;
    private String profilePicture;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
