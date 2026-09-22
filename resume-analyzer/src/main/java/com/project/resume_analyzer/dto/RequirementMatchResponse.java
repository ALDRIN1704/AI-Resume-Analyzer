package com.project.resume_analyzer.dto;

import java.util.List;

public record RequirementMatchResponse(
        List<RequirementMatch> matches
) {
}