package com.project.resume_analyzer.controller;

import java.io.IOException;
import java.util.*;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.resume_analyzer.dto.AIAnalysisResponse;
import com.project.resume_analyzer.service.AIService;
import com.project.resume_analyzer.service.ResumeParserService;
import com.project.resume_analyzer.service.TextCleaningService;

import com.project.resume_analyzer.dto.*;
import com.project.resume_analyzer.dto.MatchResult;
import com.project.resume_analyzer.service.*;

@RestController
@RequestMapping("/api")
public class ResumeController {

    private final ResumeParserService resumeParserService;
    private final TextCleaningService textCleaningService;
    private final AIService aiService;
    private final MatchingService matchingService;
    private final ScoringService scoringService;

    public ResumeController(
            ResumeParserService resumeParserService,
            TextCleaningService textCleaningService,
            AIService aiService,
            MatchingService matchingService,
            ScoringService scoringService) {

        this.resumeParserService = resumeParserService;
        this.textCleaningService = textCleaningService;
        this.aiService = aiService;
        this.matchingService = matchingService;
        this.scoringService = scoringService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok(
                "Resume Analyzer Backend is running."
        );
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeResume(
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("jobDescription") String jobDescription) {

        try {
            if (jobDescription == null
                    || jobDescription.isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body("Job description cannot be empty.");
            }

            String extractedText =
                    resumeParserService.extractText(resume);

            String cleanedText =
                    textCleaningService.cleanText(extractedText);

            AIAnalysisResponse analysis =
                    aiService.analyze(
                            cleanedText,
                            jobDescription
                    );

            MatchResult skillMatch =
                    matchingService.matchSkills(analysis);
            
            List<JobRequirement> nonSkillRequirements =
                    analysis.job()
                            .requirements()
                            .stream()
                            .filter(requirement ->
                                    !"SKILL".equalsIgnoreCase(
                                            requirement.category()
                                    )
                            )
                            .toList();
            
            RequirementMatchResponse requirementMatchResponse =
                    aiService.matchRequirements(
                            analysis.resume(),
                            nonSkillRequirements
                    );
            ATSScoreResult atsCompatibility =
                    scoringService.calculateScore(
                            analysis,
                            requirementMatchResponse.matches()
                    );
            GenerationResponse generatedContent =
                    aiService.generateContent(
                            analysis,
                            skillMatch,
                            requirementMatchResponse.matches(),
                            atsCompatibility
                    );
            AnalysisResult result =
                    new AnalysisResult(
                            analysis,
                            skillMatch,
                            requirementMatchResponse.matches(),
                            atsCompatibility,
                            generatedContent.suggestions(),
                            generatedContent.coverLetter()
                    );

            return ResponseEntity.ok(result);
            

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (IOException e) {

            return ResponseEntity
                    .internalServerError()
                    .body("Unable to process the resume.");

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body("AI analysis failed: "
                            + e.getMessage());
        }
    }
}