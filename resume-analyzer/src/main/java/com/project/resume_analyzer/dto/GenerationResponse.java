package com.project.resume_analyzer.dto;

import java.util.List;

public record GenerationResponse(
        List<String> suggestions,
        String coverLetter
) {
}