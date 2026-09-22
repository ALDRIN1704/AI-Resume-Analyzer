package com.project.resume_analyzer.dto;

import java.util.List;

public record MatchResult(
        List<String> matchedSkills,
        List<String> missingSkills,
        int matchedSkillCount,
        int totalRequiredSkills,
        double skillMatchPercentage
) {
}