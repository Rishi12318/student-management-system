package com.studentms.repository;

import com.studentms.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentAndCourse(Student student, Course course);
    List<Attendance> findByStudentAndCourseAndDateBetween(Student student, Course course, LocalDate startDate, LocalDate endDate);
    List<Attendance> findByCourseAndDateBetween(Course course, LocalDate startDate, LocalDate endDate);
    List<Attendance> findByStudentAndDateBetween(Student student, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student AND a.course = :course AND a.status = 'PRESENT'")
    long countPresentDays(@Param("student") Student student, @Param("course") Course course);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student AND a.course = :course")
    long countTotalDays(@Param("student") Student student, @Param("course") Course course);

    @Query("SELECT a.student, COUNT(a) as presentCount FROM Attendance a WHERE a.course = :course AND a.status = 'PRESENT' GROUP BY a.student")
    List<Object[]> getAttendanceSummaryByCourse(@Param("course") Course course);

    @Query("SELECT a FROM Attendance a WHERE a.date = :date AND a.course = :course")
    List<Attendance> findByDateAndCourse(@Param("date") LocalDate date, @Param("course") Course course);
}
