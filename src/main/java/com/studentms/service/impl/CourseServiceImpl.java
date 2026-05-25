package com.studentms.service.impl;

import com.studentms.dto.request.CourseRequest;
import com.studentms.dto.response.CourseResponse;
import com.studentms.dto.response.MessageResponse;
import com.studentms.exception.CourseNotFoundException;
import com.studentms.model.Course;
import com.studentms.repository.CourseRepository;
import com.studentms.repository.EnrollmentRepository;
import com.studentms.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        if (courseRepository.findByCourseCode(request.getCourseCode()).isPresent()) {
            throw new IllegalArgumentException("Course code '" + request.getCourseCode() + "' already exists");
        }
        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .courseName(request.getCourseName())
                .credits(request.getCredits())
                .semester(request.getSemester())
                .description(request.getDescription())
                .department(request.getDepartment())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .maxSeats(request.getMaxSeats() != null ? request.getMaxSeats() : 60)
                .build();
        Course saved = courseRepository.save(course);
        log.info("Created course: {}", saved.getCourseCode());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        if (request.getCourseName() != null) course.setCourseName(request.getCourseName());
        if (request.getCredits() != null) course.setCredits(request.getCredits());
        if (request.getSemester() != null) course.setSemester(request.getSemester());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getDepartment() != null) course.setDepartment(request.getDepartment());
        if (request.getIsActive() != null) course.setIsActive(request.getIsActive());
        if (request.getMaxSeats() != null) course.setMaxSeats(request.getMaxSeats());
        return mapToResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public MessageResponse deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) throw new CourseNotFoundException(id);
        courseRepository.deleteById(id);
        log.info("Deleted course: {}", id);
        return MessageResponse.builder().message("Course deleted successfully").success(true).status(200).build();
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getActiveCourses() {
        return courseRepository.findByIsActiveTrueOrderBySemester().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public CourseResponse getCourseById(Long id) {
        return mapToResponse(courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id)));
    }

    @Override
    public List<CourseResponse> getCoursesBySemester(Integer semester) {
        return courseRepository.findBySemester(semester).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> searchCourses(String keyword) {
        return courseRepository.searchCourses(keyword).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public CourseResponse mapToResponse(Course c) {
        long count = enrollmentRepository.countActiveEnrollmentsByCourse(c);
        return CourseResponse.builder()
                .id(c.getId())
                .courseCode(c.getCourseCode())
                .courseName(c.getCourseName())
                .credits(c.getCredits())
                .semester(c.getSemester())
                .description(c.getDescription())
                .department(c.getDepartment())
                .isActive(c.getIsActive())
                .maxSeats(c.getMaxSeats())
                .enrollmentCount(count)
                .createdAt(c.getCreatedAt())
                .build();
    }
}
