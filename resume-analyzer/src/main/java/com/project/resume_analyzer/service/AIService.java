package com.project.resume_analyzer.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;
import java.util.*;

import com.project.resume_analyzer.dto.*;

@Service
public class AIService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AIService(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }
    
    public RequirementMatchResponse matchRequirements(
            ResumeAnalysis resume,
            List<JobRequirement> requirements) {

        try {

            Map<String, Object> requestData = Map.of(
                    "resume", resume,
                    "requirements", requirements
            );

            String jsonBody =
                    objectMapper.writeValueAsString(requestData);

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            "http://127.0.0.1:8000/match-requirements"
                                    )
                            )
                            .version(
                                    HttpClient.Version.HTTP_1_1
                            )
                            .header(
                                    "Content-Type",
                                    "application/json; charset=UTF-8"
                            )
                            .header(
                                    "Accept",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers.ofString(
                                            jsonBody,
                                            StandardCharsets.UTF_8
                                    )
                            )
                            .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString(
                                    StandardCharsets.UTF_8
                            )
                    );

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {

                throw new RuntimeException(
                        "FastAPI requirement matching returned "
                                + response.statusCode()
                                + ": "
                                + response.body()
                );
            }

            return objectMapper.readValue(
                    response.body(),
                    RequirementMatchResponse.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to perform requirement matching: "
                            + e.getMessage(),
                    e
            );
        }
    }

    
    public GenerationResponse generateContent(
            AIAnalysisResponse analysis,
            MatchResult skillMatch,
            List<RequirementMatch> requirementMatches,
            ATSScoreResult atsScore) {

        try {

            Map<String, Object> requestData = Map.of(
                    "resume", analysis.resume(),
                    "job", analysis.job(),
                    "matchedSkills", skillMatch.matchedSkills(),
                    "missingSkills", skillMatch.missingSkills(),
                    "requirementMatches", requirementMatches,
                    "atsScore", atsScore.score()
            );

            String jsonBody =
                    objectMapper.writeValueAsString(requestData);

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            "http://127.0.0.1:8000/generate"
                                    )
                            )
                            .version(
                                    HttpClient.Version.HTTP_1_1
                            )
                            .header(
                                    "Content-Type",
                                    "application/json; charset=UTF-8"
                            )
                            .header(
                                    "Accept",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers.ofString(
                                            jsonBody,
                                            StandardCharsets.UTF_8
                                    )
                            )
                            .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString(
                                    StandardCharsets.UTF_8
                            )
                    );

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {

                throw new RuntimeException(
                        "FastAPI generation returned "
                                + response.statusCode()
                                + ": "
                                + response.body()
                );
            }

            return objectMapper.readValue(
                    response.body(),
                    GenerationResponse.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to generate suggestions and cover letter: "
                            + e.getMessage(),
                    e
            );
        }
    }
    
    
    public AIAnalysisResponse analyze(
            String resumeText,
            String jobDescription) {

        try {

            AIAnalysisRequest aiRequest =
                    new AIAnalysisRequest(
                            resumeText,
                            jobDescription
                    );

            String jsonBody =
                    objectMapper.writeValueAsString(aiRequest);

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            "http://127.0.0.1:8000/analyze"
                                    )
                            )
                            .version(
                                    HttpClient.Version.HTTP_1_1
                            )
                            .header(
                                    "Content-Type",
                                    "application/json; charset=UTF-8"
                            )
                            .header(
                                    "Accept",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers.ofString(
                                            jsonBody,
                                            StandardCharsets.UTF_8
                                    )
                            )
                            .build();

            System.out.println(
                    "Sending body length: "
                    + jsonBody.getBytes(
                            StandardCharsets.UTF_8
                    ).length
            );

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString(
                                    StandardCharsets.UTF_8
                            )
                    );

            System.out.println(
                    "FastAPI status: "
                    + response.statusCode()
            );

            System.out.println(
                    "FastAPI response: "
                    + response.body()
            );

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {

                throw new RuntimeException(
                        "FastAPI returned "
                                + response.statusCode()
                                + ": "
                                + response.body()
                );
            }

            return objectMapper.readValue(
                    response.body(),
                    AIAnalysisResponse.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to communicate with AI service: "
                            + e.getMessage(),
                    e
            );
        }
    }
}