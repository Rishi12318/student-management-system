package com.studentms.repository;

import com.studentms.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findByIsActiveTrue();
    List<Course> findByIsActiveTrueOrderBySemester();
    List<Course> findBySemester(Integer semester);
    List<Course> findByDepartment(String department);
    List<Course> findBySemesterAndIsActiveTrue(Integer semester);

    @Query("SELECT c FROM Course c WHERE c.courseCode LIKE %:keyword% OR c.courseName LIKE %:keyword%")
    List<Course> searchCourses(@Param("keyword") String keyword);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.isActive = true")
    long getActiveCourseCount();
}
