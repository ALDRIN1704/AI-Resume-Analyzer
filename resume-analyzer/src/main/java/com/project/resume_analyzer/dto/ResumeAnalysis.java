package com.project.resume_analyzer.dto;

import java.util.List;

public record ResumeAnalysis(
        List<String> skills,
        List<String> experience,
        List<String> projects,
        List<String> education,
        List<String> certifications,
        List<String> achievements,
        List<String> research,
        List<String> leadership,
        List<String> activities
) {
}