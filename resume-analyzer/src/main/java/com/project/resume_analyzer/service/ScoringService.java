package com.project.resume_analyzer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.project.resume_analyzer.dto.AIAnalysisResponse;
import com.project.resume_analyzer.dto.ATSScoreResult;
import com.project.resume_analyzer.dto.JobRequirement;
import com.project.resume_analyzer.dto.RequirementMatch;

@Service
public class ScoringService {

    public ATSScoreResult calculateScore(
            AIAnalysisResponse analysis,
            List<RequirementMatch> requirementMatches) {

        int earnedPoints = 0;
        int totalPoints = 0;

        List<String> explanation = new ArrayList<>();

        List<String> resumeSkills =
                analysis.resume().skills();

        for (JobRequirement requirement :
                analysis.job().requirements()) {

            int weight =
                    getWeight(requirement.priority());

            totalPoints += weight;

            boolean matched;

            if ("SKILL".equalsIgnoreCase(
                    requirement.category())) {

                matched = containsSkill(
                        resumeSkills,
                        requirement.name()
                );

            } else {

                matched = isRequirementMatched(
                        requirement,
                        requirementMatches
                );
            }

            if (matched) {

                earnedPoints += weight;

                explanation.add(
                        "Matched "
                                + requirement.priority()
                                + " requirement: "
                                + requirement.name()
                );

            } else {

                explanation.add(
                        "Missing "
                                + requirement.priority()
                                + " requirement: "
                                + requirement.name()
                );
            }
        }

        double score;

        if (totalPoints == 0) {
            score = 0.0;
        } else {
            score =
                    ((double) earnedPoints
                            / totalPoints) * 100.0;
        }

        score =
                Math.round(score * 100.0)
                        / 100.0;

        return new ATSScoreResult(
                score,
                earnedPoints,
                totalPoints,
                explanation
        );
    }

    private boolean containsSkill(
            List<String> resumeSkills,
            String requiredSkill) {

        if (resumeSkills == null) {
            return false;
        }

        return resumeSkills.stream()
                .anyMatch(skill ->
                        normalize(skill)
                                .equals(
                                        normalize(requiredSkill)
                                )
                );
    }

    private boolean isRequirementMatched(
            JobRequirement requirement,
            List<RequirementMatch> matches) {

        if (matches == null) {
            return false;
        }

        return matches.stream()
                .anyMatch(match ->
                        normalize(match.requirement())
                                .equals(
                                        normalize(
                                                requirement.name()
                                        )
                                )
                                && match.matched()
                );
    }

    private int getWeight(String priority) {

        if (priority == null) {
            return 1;
        }

        return switch (
                priority.toUpperCase()) {

            case "REQUIRED" -> 3;
            case "PREFERRED" -> 2;
            case "OPTIONAL" -> 1;
            default -> 1;
        };
    }

    private String normalize(String value) {

        if (value == null) {
            return "";
        }

        return value
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", " ");
    }
}