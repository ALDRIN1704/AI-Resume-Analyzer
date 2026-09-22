package com.project.resume_analyzer.dto;

public record JobRequirement(
        String name,
        String category,
        String priority
) {
}