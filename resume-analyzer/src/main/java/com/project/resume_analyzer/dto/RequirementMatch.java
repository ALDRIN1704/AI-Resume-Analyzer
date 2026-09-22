package com.project.resume_analyzer.dto;

public record RequirementMatch(
        String requirement,
        String category,
        String priority,
        boolean matched,
        String evidence
) {
}