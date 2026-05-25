package com.studentms.repository;

import com.studentms.model.Student;
import com.studentms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEnrollmentNumber(String enrollmentNumber);
    Optional<Student> findByUser(User user);
    Optional<Student> findByUserId(Long userId);
    List<Student> findAllByOrderByEnrollmentNumber();
    List<Student> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);

    @Query("SELECT s FROM Student s WHERE s.enrollmentNumber LIKE %:search% OR s.firstName LIKE %:search% OR s.lastName LIKE %:search%")
    List<Student> searchStudents(@Param("search") String search);

    @Query("SELECT COUNT(s) FROM Student s")
    long getTotalStudentCount();
}
