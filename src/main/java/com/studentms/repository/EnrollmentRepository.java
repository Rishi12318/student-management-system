package com.studentms.repository;

import com.studentms.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(Student student);
    List<Enrollment> findByCourse(Course course);
    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);
    List<Enrollment> findByStatus(EnrollmentStatus status);
    List<Enrollment> findByStudentAndStatus(Student student, EnrollmentStatus status);
    List<Enrollment> findByCourseAndStatus(Course course, EnrollmentStatus status);
    boolean existsByStudentAndCourseAndStatus(Student student, Course course, EnrollmentStatus status);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course = :course AND e.status = 'ACTIVE'")
    long countActiveEnrollmentsByCourse(@Param("course") Course course);

    @Query("SELECT e.student FROM Enrollment e WHERE e.course = :course AND e.status = 'ACTIVE'")
    List<Student> findActiveStudentsByCourse(@Param("course") Course course);

    @Query("SELECT e.course FROM Enrollment e WHERE e.student = :student AND e.status = 'ACTIVE'")
    List<Course> findActiveCoursesByStudent(@Param("student") Student student);
}
