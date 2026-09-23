# ResumeIQ — AI Resume Analyzer

AI-powered resume analysis using **Java, Spring Boot, Python FastAPI, and Groq AI**.

ResumeIQ compares a candidate's PDF/DOCX resume with a job description, identifies skill gaps, evaluates job requirements, calculates deterministic compatibility scores, provides improvement suggestions, and generates a tailored cover letter.

---

## 🚀 Live Deployment

### ResumeIQ Application

**Live App:**  
https://ai-resume-analyzer-1-g2l2.onrender.com/

### FastAPI AI Service

**Health Check:**  
https://ai-resume-analyzer-uazs.onrender.com/health

**Swagger API Documentation:**  
https://ai-resume-analyzer-uazs.onrender.com/docs

**OpenAPI Specification:**  
https://ai-resume-analyzer-uazs.onrender.com/openapi.json

> The POST endpoints require request bodies. Use the ResumeIQ application, Postman, or FastAPI Swagger to test them.

---

## 📌 Overview

ResumeIQ is a full-stack AI Resume Analyzer that compares a candidate's resume against a target job description.

The application accepts:

- PDF or DOCX resume
- Target job description

It then performs:

- Resume text extraction
- Text cleaning
- AI-based structured extraction
- Required skill matching
- Semantic requirement matching
- ATS compatibility scoring
- Resume improvement suggestions
- Cover letter generation

The system uses a hybrid architecture:

- **Java / Spring Boot** — document processing, API orchestration, matching, scoring, and frontend
- **Python / FastAPI** — AI/NLP processing
- **Groq AI** — structured extraction, semantic analysis, suggestions, and cover-letter generation
- **HTML / CSS / JavaScript** — frontend dashboard

The final scores are calculated in **Java**, not generated directly by the LLM.

---

## ✨ Key Features

| Feature | Description |
|---|---|
| Resume Upload | Supports PDF and DOCX |
| Resume Parsing | Apache PDFBox and Apache POI |
| Text Cleaning | Normalizes extracted resume text |
| AI Extraction | Extracts structured resume information |
| JD Analysis | Extracts skills and job requirements |
| Skill Matching | Finds matched and missing required skills |
| Semantic Matching | Maps job requirements to resume evidence |
| Skill Match | Calculates required skill coverage |
| ATS Compatibility | Weighted deterministic score |
| Suggestions | Generates targeted resume improvements |
| Cover Letter | Generates a tailored cover letter |
| Dashboard | Displays the complete analysis report |

---

## 🏗️ System Architecture

```text
Resume + Job Description
          |
          v
     Spring Boot
          |
          v
 Resume Validation
          |
          v
 Document Parsing
   /           \
PDFBox      Apache POI
   \           /
          v
     Text Cleaning
          |
          v
      FastAPI AI
          |
    +-----+------+
    |            |
 Resume       JD Analysis
 Analysis
    |            |
    +-----+------+
          |
          v
   Java Skill Matching
          |
          v
 Semantic Evidence Matching
          |
          v
   Java Scoring Engine
     /           \
Skill Match    ATS Score
     \           /
          v
 AI Suggestions
          |
          v
   Cover Letter
          |
          v
 ResumeIQ Dashboard
