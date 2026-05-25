package com.studentms.service.impl;

import com.studentms.dto.request.MarksRequest;
import com.studentms.dto.response.MarksResponse;
import com.studentms.dto.response.MessageResponse;
import com.studentms.exception.CourseNotFoundException;
import com.studentms.exception.StudentNotFoundException;
import com.studentms.model.*;
import com.studentms.repository.*;
import com.studentms.service.MarksService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarksServiceImpl implements MarksService {

    private final MarksRepository marksRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public MarksResponse addMarks(MarksRequest request) {
        if (request.getMarksObtained() > request.getMaxMarks()) {
            throw new IllegalArgumentException("Marks obtained cannot exceed max marks");
        }
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException(request.getStudentId()));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(request.getCourseId()));

        Marks marks = Marks.builder()
                .student(student)
                .course(course)
                .examType(request.getExamType())
                .marksObtained(request.getMarksObtained())
                .maxMarks(request.getMaxMarks())
                .examDate(request.getExamDate())
                .remarks(request.getRemarks())
                .build();
        Marks saved = marksRepository.save(marks);
        log.info("Added marks for student {} course {} exam {}", student.getEnrollmentNumber(),
                course.getCourseCode(), request.getExamType());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public MarksResponse updateMarks(Long id, MarksRequest request) {
        Marks marks = marksRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marks record not found: " + id));
        if (request.getMarksObtained() != null) {
            if (request.getMarksObtained() > marks.getMaxMarks()) {
                throw new IllegalArgumentException("Marks obtained cannot exceed max marks");
            }
            marks.setMarksObtained(request.getMarksObtained());
        }
        if (request.getMaxMarks() != null) marks.setMaxMarks(request.getMaxMarks());
        if (request.getExamDate() != null) marks.setExamDate(request.getExamDate());
        if (request.getRemarks() != null) marks.setRemarks(request.getRemarks());
        return mapToResponse(marksRepository.save(marks));
    }

    @Override
    public List<MarksResponse> getMarksByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        return marksRepository.findByStudent(student).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<MarksResponse> getMarksByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        return marksRepository.findByCourse(course).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public String calculateGrade(Double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B+";
        if (percentage >= 60) return "B";
        if (percentage >= 50) return "C";
        if (percentage >= 40) return "D";
        return "F";
    }

    @Override
    public Double getClassAverage(Long courseId, String examType) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        ExamType type = ExamType.valueOf(examType.toUpperCase());
        Double avg = marksRepository.getAveragePercentageForExam(course, type);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;
    }

    @Override
    @Transactional
    public MessageResponse deleteMarks(Long id) {
        if (!marksRepository.existsById(id)) throw new RuntimeException("Marks record not found: " + id);
        marksRepository.deleteById(id);
        return MessageResponse.builder().message("Marks deleted successfully").success(true).status(200).build();
    }

    private MarksResponse mapToResponse(Marks m) {
        double pct = m.getMaxMarks() > 0 ? (m.getMarksObtained() / m.getMaxMarks()) * 100 : 0;
        return MarksResponse.builder()
                .id(m.getId())
                .studentId(m.getStudent().getId())
                .studentName(m.getStudent().getFullName())
                .courseId(m.getCourse().getId())
                .courseName(m.getCourse().getCourseName())
                .examType(m.getExamType())
                .marksObtained(m.getMarksObtained())
                .maxMarks(m.getMaxMarks())
                .percentage(Math.round(pct * 100.0) / 100.0)
                .grade(calculateGrade(pct))
                .examDate(m.getExamDate())
                .remarks(m.getRemarks())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
