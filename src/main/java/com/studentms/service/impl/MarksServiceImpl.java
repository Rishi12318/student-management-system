package com.studentms.service.impl;

import com.studentms.dto.request.MarksRequest;
import com.studentms.dto.response.MarksResponse;
import com.studentms.exception.ResourceNotFoundException;
import com.studentms.model.Course;
import com.studentms.model.Marks;
import com.studentms.model.Student;
import com.studentms.repository.CourseRepository;
import com.studentms.repository.MarksRepository;
import com.studentms.repository.StudentRepository;
import com.studentms.service.MarksService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarksServiceImpl implements MarksService {
    
    private final MarksRepository marksRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    
    @Override
    public MarksResponse addMarks(MarksRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        Marks marks = Marks.builder()
                .student(student)
                .course(course)
                .examType(request.getExamType())
                .marksObtained(request.getMarksObtained())
                .maxMarks(request.getMaxMarks())
                .examDate(request.getExamDate())
                .remarks(request.getRemarks())
                .build();
        
        marks = marksRepository.save(marks);
        
        double percentage = (marks.getMarksObtained() / marks.getMaxMarks()) * 100;
        String grade = calculateGrade(percentage);
        
        return MarksResponse.builder()
                .id(marks.getId())
                .studentId(marks.getStudent().getId())
                .studentName(marks.getStudent().getFullName())
                .enrollmentNumber(marks.getStudent().getEnrollmentNumber())
                .courseId(marks.getCourse().getId())
                .courseCode(marks.getCourse().getCourseCode())
                .courseName(marks.getCourse().getCourseName())
                .examType(marks.getExamType())
                .marksObtained(marks.getMarksObtained())
                .maxMarks(marks.getMaxMarks())
                .percentage(percentage)
                .grade(grade)
                .examDate(marks.getExamDate())
                .remarks(marks.getRemarks())
                .build();
    }
    
    private String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B+";
        if (percentage >= 60) return "B";
        if (percentage >= 50) return "C";
        if (percentage >= 40) return "D";
        return "F";
    }
}