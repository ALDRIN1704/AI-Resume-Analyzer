package com.project.resume_analyzer.dto;

public record AIAnalysisRequest(
        String resumeText,
        String jobDescription
) {
}