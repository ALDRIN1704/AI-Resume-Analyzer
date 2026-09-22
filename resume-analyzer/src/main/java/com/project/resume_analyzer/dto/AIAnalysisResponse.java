package com.project.resume_analyzer.dto;

public record AIAnalysisResponse(
        ResumeAnalysis resume,
        JobAnalysis job
) {
}