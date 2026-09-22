package com.project.resume_analyzer.dto;

import java.util.List;

public record AnalysisResult(
        AIAnalysisResponse analysis,
        MatchResult skillMatch,
        List<RequirementMatch> requirementMatches,
        ATSScoreResult atsCompatibility,
        List<String> suggestions,
        String coverLetter
) {
}