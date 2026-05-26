package com.studentms.service;

import com.studentms.dto.request.MarksRequest;
import com.studentms.dto.response.MarksResponse;

public interface MarksService {
    MarksResponse addMarks(MarksRequest request);
}