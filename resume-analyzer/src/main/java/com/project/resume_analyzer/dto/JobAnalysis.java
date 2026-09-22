package com.project.resume_analyzer.dto;

import java.util.List;

public record JobAnalysis(
        List<String> requiredSkills,
        List<String> preferredSkills,
        List<JobRequirement> requirements
) {
}