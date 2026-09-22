package com.project.resume_analyzer.dto;

import java.util.List;

public record ATSScoreResult(
        double score,
        int earnedPoints,
        int totalPoints,
        List<String> explanation
) {
}