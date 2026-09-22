package com.project.resume_analyzer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.project.resume_analyzer.dto.AIAnalysisResponse;
import com.project.resume_analyzer.dto.MatchResult;

@Service
public class MatchingService {

    public MatchResult matchSkills(AIAnalysisResponse analysis) {

        List<String> resumeSkills = analysis.resume().skills();
        List<String> requiredSkills = analysis.job().requiredSkills();

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String requiredSkill : requiredSkills) {

            boolean matched = resumeSkills.stream()
                    .anyMatch(resumeSkill ->
                            normalize(resumeSkill)
                                    .equals(normalize(requiredSkill)));

            if (matched) {
                matchedSkills.add(requiredSkill);
            } else {
                missingSkills.add(requiredSkill);
            }
        }

        int matchedCount = matchedSkills.size();
        int totalRequired = requiredSkills.size();

        double percentage = totalRequired == 0
                ? 0.0
                : ((double) matchedCount / totalRequired) * 100;

        percentage = Math.round(percentage * 100.0) / 100.0;

        return new MatchResult(
                matchedSkills,
                missingSkills,
                matchedCount,
                totalRequired,
                percentage
        );
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