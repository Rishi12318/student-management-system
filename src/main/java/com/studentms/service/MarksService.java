package com.studentms.service;

import com.studentms.dto.request.MarksRequest;
import com.studentms.dto.response.MarksResponse;
import com.studentms.dto.response.MessageResponse;

import java.util.List;

public interface MarksService {
    MarksResponse addMarks(MarksRequest request);
    MarksResponse updateMarks(Long id, MarksRequest request);
    List<MarksResponse> getMarksByStudent(Long studentId);
    List<MarksResponse> getMarksByCourse(Long courseId);
    String calculateGrade(Double percentage);
    Double getClassAverage(Long courseId, String examType);
    MessageResponse deleteMarks(Long id);
}
