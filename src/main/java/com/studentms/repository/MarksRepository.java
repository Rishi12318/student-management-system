package com.studentms.repository;

import com.studentms.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarksRepository extends JpaRepository<Marks, Long> {
    List<Marks> findByStudent(Student student);
    List<Marks> findByCourse(Course course);
    List<Marks> findByStudentAndCourse(Student student, Course course);
    Optional<Marks> findByStudentAndCourseAndExamType(Student student, Course course, ExamType examType);
    List<Marks> findByExamType(ExamType examType);

    @Query("SELECT AVG(m.marksObtained * 100.0 / m.maxMarks) FROM Marks m WHERE m.course = :course AND m.examType = :examType")
    Double getAveragePercentageForExam(@Param("course") Course course, @Param("examType") ExamType examType);

    @Query("SELECT m.student, SUM(m.marksObtained), SUM(m.maxMarks) FROM Marks m WHERE m.student = :student GROUP BY m.student")
    Object getTotalMarksForStudent(@Param("student") Student student);

    @Query("SELECT m FROM Marks m WHERE m.student = :student ORDER BY m.examDate DESC")
    List<Marks> findLatestMarksByStudent(@Param("student") Student student);

    @Query("SELECT AVG(m.marksObtained * 100.0 / m.maxMarks) FROM Marks m WHERE m.student = :student")
    Double getOverallPercentageForStudent(@Param("student") Student student);
}
