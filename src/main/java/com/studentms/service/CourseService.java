package com.studentms.service;

import com.studentms.dto.request.CourseRequest;
import com.studentms.dto.response.CourseResponse;
import com.studentms.dto.response.MessageResponse;

import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CourseRequest request);
    CourseResponse updateCourse(Long id, CourseRequest request);
    MessageResponse deleteCourse(Long id);
    List<CourseResponse> getAllCourses();
    List<CourseResponse> getActiveCourses();
    CourseResponse getCourseById(Long id);
    List<CourseResponse> getCoursesBySemester(Integer semester);
    List<CourseResponse> searchCourses(String keyword);
}
