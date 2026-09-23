

# Complete Development Checkpoints

ResumeIQ was developed using a checkpoint-driven approach.

Each major responsibility was implemented and tested independently before
being integrated into the complete resume-analysis pipeline.

---

# 1. Spring Boot / Java Checkpoints

## 1.1 Application Setup

| ID | Checkpoint | Expected Result |
|---|---|---|
| J01 | Java 21 configured | Application runs using Java 21 |
| J02 | Spring Boot project created | Application starts without configuration errors |
| J03 | Maven configured | Required dependencies resolve successfully |
| J04 | Spring Web MVC configured | REST endpoints can be exposed |
| J05 | Static resources configured | HTML/CSS/JS can be served by Spring Boot |
| J06 | Multipart configuration | Resume files can be uploaded |
| J07 | Upload limit configured | Maximum request/file size is 10 MB |
| J08 | Dynamic server port | `${PORT:8081}` supports local and cloud execution |

### Checkpoint

```text
Java 21
   ↓
Spring Boot
   ↓
Maven Dependencies
   ↓
Application Startup
   ↓
HTTP Server Available
```

---

## 1.2 Resume Upload Validation

| ID | Checkpoint | Expected Result |
|---|---|---|
| J09 | Resume parameter accepted | Multipart resume reaches controller |
| J10 | JD parameter accepted | Job description reaches controller |
| J11 | Empty resume validation | Empty upload is rejected |
| J12 | Filename validation | Invalid/missing filename is rejected |
| J13 | PDF validation | `.pdf` files are accepted |
| J14 | DOCX validation | `.docx` files are accepted |
| J15 | Unsupported format validation | Other file formats are rejected |

### Checkpoint

```text
POST /api/analyze
        ↓
Resume + Job Description
        ↓
File Exists?
        ↓
Valid Filename?
        ↓
PDF / DOCX?
        ↓
Continue Processing
```

---

## 1.3 PDF Parsing

| ID | Checkpoint | Expected Result |
|---|---|---|
| J16 | PDFBox integration | PDFBox dependency loads correctly |
| J17 | PDF bytes loaded | Uploaded PDF can be opened |
| J18 | PDF text extraction | Text-based PDF produces readable text |
| J19 | Empty PDF detection | PDF without readable text is rejected |
| J20 | Scanned PDF handling | Image-only/scanned PDF returns a clear warning |
| J21 | Resource cleanup | PDF document is closed after processing |

### Checkpoint

```text
PDF Resume
    ↓
Apache PDFBox
    ↓
Load PDF
    ↓
PDFTextStripper
    ↓
Readable Text?
   / \
 Yes  No
  ↓    ↓
Text  Validation Error
```

---

## 1.4 DOCX Parsing

| ID | Checkpoint | Expected Result |
|---|---|---|
| J22 | Apache POI integration | POI dependency loads correctly |
| J23 | DOCX document opened | Uploaded DOCX can be read |
| J24 | Paragraph extraction | Normal paragraph text is extracted |
| J25 | Table extraction | Text contained in tables is extracted |
| J26 | Empty DOCX detection | DOCX without readable content is rejected |
| J27 | Resource cleanup | Input stream/document closes correctly |

### Checkpoint

```text
DOCX Resume
     ↓
Apache POI
     ↓
Paragraphs ──┐
             ├──→ Extracted Resume Text
Tables ──────┘
```

---

## 1.5 Resume Text Cleaning

| ID | Checkpoint | Expected Result |
|---|---|---|
| J28 | Windows line endings | `\r\n` normalized |
| J29 | Old-style line endings | `\r` normalized |
| J30 | Non-breaking spaces | Converted into normal spaces |
| J31 | Repeated spaces | Excess whitespace removed |
| J32 | Blank lines | Excess blank lines reduced |
| J33 | Leading/trailing whitespace | Removed |
| J34 | Technical punctuation | Important technical terms remain intact |

Technical terms such as:

```text
C++
C#
.NET
Node.js
CGPA
```

are deliberately preserved.

### Checkpoint

```text
Raw Resume Text
       ↓
TextCleaningService
       ↓
Normalize Whitespace
       ↓
Preserve Technical Terms
       ↓
Clean Resume Text
```

---

# 2. Java → FastAPI Communication Checkpoints

## 2.1 AI Service Configuration

| ID | Checkpoint | Expected Result |
|---|---|---|
| J35 | Local AI URL | Defaults to `http://127.0.0.1:8000` |
| J36 | Production AI URL | Reads `AI_SERVICE_URL` environment variable |
| J37 | HTTP client creation | Java `HttpClient` initializes correctly |
| J38 | HTTP version | Communication uses HTTP/1.1 |
| J39 | UTF-8 request | JSON body uses UTF-8 |
| J40 | Content-Type | Request sends `application/json` |
| J41 | Accept header | Java accepts JSON responses |

### Checkpoint

```text
Local
AI_SERVICE_URL absent
        ↓
http://127.0.0.1:8000

Production
AI_SERVICE_URL configured
        ↓
Deployed FastAPI URL
```

---

## 2.2 `/analyze` Integration

| ID | Checkpoint | Expected Result |
|---|---|---|
| J42 | Request DTO | Resume text and JD mapped into request |
| J43 | JSON serialization | Request converts to valid JSON |
| J44 | `/analyze` request | FastAPI receives request |
| J45 | HTTP success validation | Java accepts successful 2xx response |
| J46 | JSON deserialization | Response maps to `AIAnalysisResponse` |
| J47 | Resume DTO mapping | Resume information maps correctly |
| J48 | Job DTO mapping | Job information maps correctly |
| J49 | Requirement DTO mapping | Requirements map correctly |

---

# 3. FastAPI / Python Checkpoints

## 3.1 FastAPI Setup

| ID | Checkpoint | Expected Result |
|---|---|---|
| P01 | Python environment | Python service runs successfully |
| P02 | FastAPI installed | Application imports correctly |
| P03 | Uvicorn configured | ASGI server starts |
| P04 | Pydantic configured | Request/response models validate |
| P05 | Groq SDK configured | AI provider client initializes |
| P06 | `.env` support | Local environment variables load |
| P07 | Production environment | Cloud environment variables load |
| P08 | API key protection | Groq API key is not hardcoded |
| P09 | `/health` | Health endpoint returns successful response |
| P10 | `/docs` | Swagger/OpenAPI interface is available |

### Checkpoint

```text
FastAPI
  │
  ├── /health
  ├── /analyze
  ├── /match-requirements
  └── /generate
```

---

## 3.2 Pydantic Model Validation

| ID | Checkpoint | Expected Result |
|---|---|---|
| P11 | AnalysisRequest | Resume text and JD validated |
| P12 | ResumeAnalysis | Resume fields validated |
| P13 | JobAnalysis | Job fields validated |
| P14 | JobRequirement | Requirement fields validated |
| P15 | AIAnalysisResponse | Complete extraction response validated |
| P16 | RequirementMatchRequest | Semantic-match request validated |
| P17 | RequirementMatch | Individual evidence result validated |
| P18 | RequirementMatchResponse | Match collection validated |
| P19 | GenerationRequest | Generation input validated |
| P20 | GenerationResponse | Suggestions/cover letter validated |

---

# 4. AI Structured Extraction Checkpoints

## 4.1 Resume Analysis

The AI extraction stage identifies:

| ID | Resume Field | Expected Result |
|---|---|---|
| A01 | Skills | Technical skills extracted |
| A02 | Experience | Work/internship experience extracted |
| A03 | Projects | Resume projects extracted |
| A04 | Education | Education extracted |
| A05 | Certifications | Certifications extracted |
| A06 | Achievements | Achievements extracted |
| A07 | Research | Research/publications extracted |
| A08 | Leadership | Leadership experience extracted |
| A09 | Activities | Relevant activities extracted |

### Structured Output Checkpoint

```json
{
  "skills": [],
  "experience": [],
  "projects": [],
  "education": [],
  "certifications": [],
  "achievements": [],
  "research": [],
  "leadership": [],
  "activities": []
}
```

All resume collections are returned as predictable arrays of strings.

---

## 4.2 Job Description Analysis

| ID | Checkpoint | Expected Result |
|---|---|---|
| A10 | Required skills | Mandatory skills extracted |
| A11 | Preferred skills | Preferred skills extracted |
| A12 | Requirements | Job requirements extracted |
| A13 | Requirement name | Human-readable requirement preserved |
| A14 | Category | Requirement categorized |
| A15 | Priority | Requirement priority determined |

Supported categories:

```text
SKILL
EXPERIENCE
PROJECT
EDUCATION
CERTIFICATION
RESEARCH
ACHIEVEMENT
LEADERSHIP
ACTIVITY
OTHER
```

Supported priorities:

```text
REQUIRED
PREFERRED
OPTIONAL
```

### Checkpoint

```text
Job Description
       ↓
LLM Analysis
       ↓
Required Skills
Preferred Skills
Requirements
       ↓
Category + Priority
```

---

# 5. Java Skill Matching Checkpoints

Required skill matching is deliberately performed in Java rather than
allowing the LLM to calculate the score.

| ID | Checkpoint | Expected Result |
|---|---|---|
| M01 | Required skills loaded | JD required skills available |
| M02 | Resume skills loaded | Extracted resume skills available |
| M03 | Skill normalization | Case and whitespace normalized |
| M04 | Skill comparison | Required skill compared with resume skills |
| M05 | Matched classification | Existing skill added to matched list |
| M06 | Missing classification | Unsupported skill added to missing list |
| M07 | Match count | Number of matched skills calculated |
| M08 | Required count | Total required skills calculated |
| M09 | Zero handling | Division by zero avoided |
| M10 | Percentage | Skill Match percentage calculated |
| M11 | Rounding | Percentage rounded consistently |

### Formula

```text
Skill Match % =
Matched Required Skills
──────────────────────── × 100
Total Required Skills
```

### Checkpoint

```text
Required Skills
      ↓
Normalize
      ↓
Compare with Resume Skills
      ↓
 ┌────┴────┐
 ↓         ↓
Matched   Missing
 ↓
Matched Count / Total Count
 ↓
Skill Match %
```

---

# 6. Semantic Requirement Matching Checkpoints

String matching alone is insufficient for broader requirements such as
experience, education and project requirements.

| ID | Checkpoint | Expected Result |
|---|---|---|
| S01 | Skill requirements separated | Deterministic skills remain in Java |
| S02 | Non-skill requirements collected | Semantic requirements sent to AI |
| S03 | Request serialized | Requirement request becomes valid JSON |
| S04 | `/match-requirements` called | FastAPI receives semantic-match request |
| S05 | Resume evidence inspected | AI searches supplied resume information |
| S06 | Semantic equivalence | Similar meaning can match despite wording differences |
| S07 | Match decision | Requirement gets true/false result |
| S08 | Evidence generation | Matched requirement includes supporting evidence |
| S09 | Missing evidence | Unsupported requirement receives empty evidence |
| S10 | Category preservation | Original category is preserved |
| S11 | Priority preservation | Original priority is preserved |
| S12 | Response validation | Results satisfy Pydantic model |
| S13 | Java deserialization | Results map to Java DTOs |

Example:

```json
{
  "requirement": "Backend application development experience",
  "category": "EXPERIENCE",
  "priority": "REQUIRED",
  "matched": true,
  "evidence": "Resume evidence supporting the requirement"
}
```

---

# 7. ATS Compatibility Scoring Checkpoints

ATS Compatibility is an application-defined deterministic compatibility
metric calculated by Java.

The LLM does not assign the final score.

## Priority Weights

| Priority | Weight |
|---|---:|
| REQUIRED | 3 |
| PREFERRED | 2 |
| OPTIONAL | 1 |

## Scoring Checkpoints

| ID | Checkpoint | Expected Result |
|---|---|---|
| SC01 | Requirements loaded | Extracted requirements available |
| SC02 | Required weight | REQUIRED receives weight 3 |
| SC03 | Preferred weight | PREFERRED receives weight 2 |
| SC04 | Optional weight | OPTIONAL receives weight 1 |
| SC05 | Skill evidence | Skill requirements checked deterministically |
| SC06 | Semantic evidence | Non-skill results use semantic matching |
| SC07 | Earned points | Matched requirement weight added |
| SC08 | Total points | Every requirement contributes possible points |
| SC09 | Duplicate prevention | Requirement is scored once |
| SC10 | Zero handling | Empty requirements do not cause division error |
| SC11 | Score calculation | Compatibility percentage calculated |
| SC12 | Score rounding | Result rounded consistently |
| SC13 | Explanation | Matched/missing explanation produced |

### Formula

```text
ATS Compatibility =
Earned Weighted Points
────────────────────── × 100
Total Weighted Points
```

### Checkpoint

```text
Job Requirements
       ↓
Priority Weight
       ↓
Resume Evidence
       ↓
Matched?
  ┌────┴────┐
 Yes        No
  ↓          ↓
Earn Weight  0
      ↓
Earned / Total
      ↓
ATS Compatibility %
```

---

# 8. AI Suggestion Checkpoints

After deterministic matching and scoring, results are sent back to FastAPI.

| ID | Checkpoint | Expected Result |
|---|---|---|
| G01 | Resume data included | AI receives extracted resume |
| G02 | JD data included | AI receives analyzed JD |
| G03 | Matched skills included | Existing strengths available |
| G04 | Missing skills included | Skill gaps available |
| G05 | Evidence included | Requirement results available |
| G06 | ATS score included | Compatibility context available |
| G07 | Suggestions generated | Practical recommendations returned |
| G08 | JD relevance | Suggestions target supplied JD |
| G09 | Gap awareness | Missing requirements influence suggestions |
| G10 | No fabrication | Suggestions do not encourage fake qualifications |
| G11 | Response validation | Suggestions map to expected response |

---

# 9. Cover Letter Checkpoints

| ID | Checkpoint | Expected Result |
|---|---|---|
| C01 | Resume-grounded generation | Letter uses supplied resume facts |
| C02 | JD alignment | Letter targets supplied job |
| C03 | Relevant skills | Matching skills emphasized |
| C04 | Relevant projects | Applicable projects highlighted |
| C05 | Relevant experience | Supported experience highlighted |
| C06 | Missing-skill protection | Missing skills are not falsely claimed |
| C07 | No invented employer facts | Unknown company information is not fabricated |
| C08 | Professional output | Letter is readable and professional |
| C09 | Plain-text output | Unnecessary Markdown formatting avoided |
| C10 | Response returned | Java receives generated letter |

### Checkpoint

```text
Resume Facts
     +
Job Requirements
     +
Match Results
     ↓
FastAPI /generate
     ↓
Suggestions
     +
Grounded Cover Letter
```

---

# 10. Spring Boot Final Response Checkpoints

| ID | Checkpoint | Expected Result |
|---|---|---|
| R01 | AI analysis available | Structured resume/JD present |
| R02 | Skill match available | Matched/missing skills present |
| R03 | Requirement matches available | Evidence results present |
| R04 | ATS score available | Weighted score present |
| R05 | Suggestions available | Improvement list present |
| R06 | Cover letter available | Generated letter present |
| R07 | AnalysisResult assembled | All components combined |
| R08 | JSON response returned | Frontend receives complete response |

### Final Response Structure

```text
AnalysisResult
│
├── analysis
├── skillMatch
├── requirementMatches
├── atsCompatibility
├── suggestions
└── coverLetter
```

---

# 11. Spring Boot Error-Handling Checkpoints

| ID | Failure | Expected Handling |
|---|---|---|
| E01 | Empty resume | Validation error |
| E02 | Unsupported extension | Validation error |
| E03 | Empty filename | Validation error |
| E04 | Unreadable PDF | Processing error |
| E05 | Scanned PDF | Clear readable-text warning |
| E06 | Empty DOCX | Validation error |
| E07 | AI service unavailable | Communication failure returned |
| E08 | Non-2xx AI response | AI service error handled |
| E09 | Invalid AI JSON | Deserialization/processing failure handled |
| E10 | Unexpected backend failure | Internal error returned |

---

# 12. FastAPI Error-Handling Checkpoints

| ID | Failure | Expected Handling |
|---|---|---|
| PE01 | Invalid request | Pydantic validation error |
| PE02 | Invalid resume/JD input | Request rejected |
| PE03 | Missing Groq configuration | Provider initialization/call fails clearly |
| PE04 | Groq request failure | Runtime/API error propagated |
| PE05 | Invalid LLM JSON | Structured-response processing fails safely |
| PE06 | Invalid `/analyze` response | Response validation prevents malformed output |
| PE07 | Invalid semantic response | Requirement response validation catches error |
| PE08 | Generation failure | `/generate` returns appropriate error |

---

# 13. Frontend Checkpoints

| ID | Checkpoint | Expected Result |
|---|---|---|
| F01 | Application loads | ResumeIQ dashboard displays |
| F02 | Resume selector | User can choose PDF/DOCX |
| F03 | Drag-and-drop | Resume can be dropped into upload area |
| F04 | File type check | Invalid file is rejected |
| F05 | File size check | Oversized file is rejected |
| F06 | JD input | User can enter job description |
| F07 | JD character limit | UI limits JD input to configured length |
| F08 | Analyze button | Analysis request starts |
| F09 | FormData creation | Resume and JD are correctly included |
| F10 | `/api/analyze` call | Browser sends request to Spring Boot |
| F11 | Loading state | User receives progress/loading feedback |
| F12 | Error state | Backend errors are shown clearly |
| F13 | Skill Match rendering | Count and percentage displayed |
| F14 | ATS rendering | Compatibility score displayed |
| F15 | Matched skills | Matched skills displayed |
| F16 | Missing skills | Missing skills displayed |
| F17 | Requirement evidence | Semantic evidence displayed |
| F18 | Suggestions | Improvement suggestions displayed |
| F19 | Cover letter | Generated letter displayed |
| F20 | Copy functionality | Cover letter can be copied |
| F21 | Reset/new analysis | User can perform another analysis |
| F22 | Responsive layout | Dashboard works across screen sizes |

---

# 14. API Endpoint Checkpoints

## Spring Boot

### Complete Analysis

```http
POST /api/analyze
```

Content type:

```text
multipart/form-data
```

Parameters:

```text
resume          → PDF/DOCX file
jobDescription  → Text
```

Checkpoint:

```text
Postman
   ↓
POST /api/analyze
   ↓
Complete AnalysisResult
```

---

## FastAPI

### Health

```http
GET /health
```

Checkpoint:

```text
Service reachable → health response
```

### Structured Analysis

```http
POST /analyze
```

Checkpoint:

```text
Resume + JD
    ↓
Structured AIAnalysisResponse
```

### Semantic Requirement Matching

```http
POST /match-requirements
```

Checkpoint:

```text
Resume + Requirements
       ↓
RequirementMatchResponse
```

### Content Generation

```http
POST /generate
```

Checkpoint:

```text
Analysis + Match Results + Score
       ↓
Suggestions + Cover Letter
```

---

# 15. Java ↔ Python Integration Checkpoints

| ID | Integration | Expected Result |
|---|---|---|
| I01 | Java → `/analyze` | Structured analysis returned |
| I02 | FastAPI → Groq | AI provider responds |
| I03 | FastAPI → Java | JSON maps into Java DTO |
| I04 | Java Matching | Required skills evaluated |
| I05 | Java → `/match-requirements` | Semantic request succeeds |
| I06 | FastAPI Evidence → Java | Evidence maps into Java DTO |
| I07 | Java Scoring | Deterministic scores calculated |
| I08 | Java → `/generate` | Generation request succeeds |
| I09 | FastAPI → Java | Suggestions and letter returned |
| I10 | Java → Browser | Complete report returned |

---

# 16. Local Execution Checkpoints

## FastAPI

```text
Python Virtual Environment
        ↓
Dependencies Installed
        ↓
GROQ_API_KEY
        ↓
Uvicorn
        ↓
127.0.0.1:8000
        ↓
/health
        ↓
/docs
```

Expected:

```text
GET http://127.0.0.1:8000/health
```

---

## Spring Boot

```text
Java 21
   ↓
Maven
   ↓
Spring Boot
   ↓
localhost:8081
```

Expected:

```text
http://localhost:8081/
```

---

## Local End-to-End

```text
Browser
   ↓
localhost:8081
   ↓
Spring Boot
   ↓
127.0.0.1:8000
   ↓
FastAPI
   ↓
Groq
   ↓
Complete Analysis
```

---

# 17. Security Checkpoints

| ID | Checkpoint | Expected Result |
|---|---|---|
| SEC01 | `.env` ignored | Secret file not committed |
| SEC02 | API key externalized | Groq key comes from environment |
| SEC03 | `.venv` ignored | Virtual environment not committed |
| SEC04 | `target/` ignored | Maven build output not committed |
| SEC05 | No hardcoded Groq key | Source code contains no production secret |
| SEC06 | Production secret | Groq key stored in host environment |
| SEC07 | AI URL externalized | Production FastAPI URL is configurable |
| SEC08 | Upload restrictions | Only supported resume formats accepted |
| SEC09 | File size restriction | Large uploads limited |
| SEC10 | Grounded generation | AI instructed not to invent qualifications |

---

# 18. Git / GitHub Checkpoints

| ID | Checkpoint | Expected Result |
|---|---|---|
| V01 | Repository initialized | Git tracks project |
| V02 | `.gitignore` | Generated/private files excluded |
| V03 | Spring Boot source | Java project committed |
| V04 | FastAPI source | Python service committed |
| V05 | Frontend source | HTML/CSS/JS committed |
| V06 | Documentation | README and screenshots committed |
| V07 | Requirements | Python dependencies committed |
| V08 | Dockerfile | Java deployment configuration committed |
| V09 | Main branch | Latest stable code available |
| V10 | Secret verification | `.env` absent from repository |

---

# 19. FastAPI Deployment Checkpoints

| ID | Checkpoint | Expected Result |
|---|---|---|
| DP01 | Python service root | `resume-ai-service` configured |
| DP02 | Requirements file | `requirements.txt` available |
| DP03 | Dependency installation | Production dependencies install |
| DP04 | Start command | Uvicorn starts application |
| DP05 | Host binding | Service listens on `0.0.0.0` |
| DP06 | Dynamic port | Uvicorn uses `$PORT` |
| DP07 | Groq environment | `GROQ_API_KEY` configured |
| DP08 | Build | Deployment build succeeds |
| DP09 | Startup | FastAPI starts successfully |
| DP10 | Health endpoint | Production `/health` responds |
| DP11 | Swagger endpoint | Production `/docs` loads |
| DP12 | AI request | Production service can reach Groq |

Production FastAPI:

```text
https://ai-resume-analyzer-uazs.onrender.com
```

Health:

```text
https://ai-resume-analyzer-uazs.onrender.com/health
```

Swagger:

```text
https://ai-resume-analyzer-uazs.onrender.com/docs
```

---

# 20. Spring Boot Docker Checkpoints

| ID | Checkpoint | Expected Result |
|---|---|---|
| DJ01 | Dockerfile created | Java deployment is containerized |
| DJ02 | Maven build stage | Project compiles inside container |
| DJ03 | JAR generated | Spring Boot executable JAR created |
| DJ04 | Java 21 runtime | Runtime container uses Java 21 |
| DJ05 | JAR copied | Built JAR copied into runtime image |
| DJ06 | Application startup | `java -jar app.jar` starts service |
| DJ07 | Dynamic port | Spring Boot listens on cloud `$PORT` |

### Docker Pipeline

```text
Source Code
    ↓
Maven + Java 21 Build Image
    ↓
mvn clean package
    ↓
Spring Boot JAR
    ↓
Java 21 Runtime Image
    ↓
java -jar app.jar
```

---

# 21. Spring Boot Deployment Checkpoints

| ID | Checkpoint | Expected Result |
|---|---|---|
| DS01 | Java service root | `resume-analyzer` configured |
| DS02 | Docker deployment | Dockerfile detected |
| DS03 | Docker build | Java application builds |
| DS04 | `PORT` environment | Dynamic production port used |
| DS05 | `AI_SERVICE_URL` | Production FastAPI URL configured |
| DS06 | Application startup | Spring Boot starts successfully |
| DS07 | Static frontend | ResumeIQ page loads |
| DS08 | `/api/analyze` | Production Java endpoint available |
| DS09 | Java → FastAPI | Production service communication succeeds |
| DS10 | Full analysis | Deployed application generates complete report |

Production application:

```text
https://ai-resume-analyzer-1-g2l2.onrender.com/
```

Production API:

```text
POST https://ai-resume-analyzer-1-g2l2.onrender.com/api/analyze
```

---

# 22. Production End-to-End Checkpoint

The final production pipeline is:

```text
                        USER
                          │
                          ▼
             ResumeIQ Web Application
                          │
                          ▼
              Spring Boot / Java
                          │
              ┌───────────┴───────────┐
              │                       │
              ▼                       ▼
        Resume Parsing          Text Cleaning
        PDFBox / POI                  │
              └───────────┬───────────┘
                          │
                          ▼
                 FastAPI /analyze
                          │
                          ▼
                       Groq LLM
                          │
                          ▼
             Structured Resume + JD
                          │
                          ▼
                Java Skill Matching
                          │
                          ▼
          FastAPI /match-requirements
                          │
                          ▼
               Semantic Evidence
                          │
                          ▼
                Java Scoring Engine
                  │               │
                  ▼               ▼
             Skill Match    ATS Compatibility
                  └───────┬───────┘
                          │
                          ▼
                 FastAPI /generate
                    │            │
                    ▼            ▼
              Suggestions   Cover Letter
                    └──────┬─────┘
                           │
                           ▼
                  AnalysisResult
                           │
                           ▼
                 ResumeIQ Dashboard
```

---

# 23. Final Acceptance Checkpoints

The application is considered end-to-end functional when all of the following
conditions are satisfied:

- [x] Spring Boot application starts successfully
- [x] FastAPI service starts successfully
- [x] PDF resumes can be parsed
- [x] DOCX resumes can be parsed
- [x] Invalid resume formats are rejected
- [x] Scanned/unreadable PDFs return a clear error
- [x] Resume text is cleaned before AI processing
- [x] FastAPI extracts structured resume information
- [x] FastAPI extracts structured JD requirements
- [x] Required and preferred skills are separated
- [x] Requirements receive categories and priorities
- [x] Java deterministically matches required skills
- [x] Matched skills are identified
- [x] Missing skills are identified
- [x] Skill Match percentage is calculated in Java
- [x] Non-skill requirements undergo semantic evidence matching
- [x] Requirement evidence is returned
- [x] Java calculates weighted ATS Compatibility
- [x] LLM does not directly determine the final score
- [x] Resume-improvement suggestions are generated
- [x] Suggestions do not encourage fabricated qualifications
- [x] Cover letter is generated from resume-supported information
- [x] Complete result is returned to the frontend
- [x] Dashboard renders Skill Match
- [x] Dashboard renders ATS Compatibility
- [x] Dashboard renders matched and missing skills
- [x] Dashboard renders requirement evidence
- [x] Dashboard renders improvement suggestions
- [x] Dashboard renders the cover letter
- [x] Cover letter can be copied from the interface
- [x] Spring Boot API can be tested independently
- [x] FastAPI endpoints can be tested independently through Swagger
- [x] Environment variables are used for secrets
- [x] Groq API key is excluded from Git
- [x] FastAPI is deployed independently
- [x] Spring Boot is containerized using Docker
- [x] Spring Boot is deployed independently
- [x] Java uses `AI_SERVICE_URL` in production
- [x] FastAPI uses `GROQ_API_KEY` in production
- [x] Production Java service communicates with production FastAPI
- [x] Complete deployed resume analysis succeeds

---

# Final System Checkpoint

```text
Resume Upload
     ✓
File Validation
     ✓
PDF / DOCX Parsing
     ✓
Text Cleaning
     ✓
Structured AI Extraction
     ✓
Required Skill Matching
     ✓
Missing Skill Detection
     ✓
Semantic Requirement Matching
     ✓
Evidence Mapping
     ✓
Deterministic Skill Match
     ✓
Deterministic ATS Compatibility
     ✓
Improvement Suggestions
     ✓
Grounded Cover Letter
     ✓
Frontend Dashboard
     ✓
Java ↔ Python Integration
     ✓
Docker Deployment
     ✓
Cloud Deployment
     ✓
END-TO-END RESUME ANALYSIS
```

One important README correction: your old Current Limitations/Future Enhancements still says cloud deployment and Docker containerization are not included/planned. Since you've now deployed both services and containerized Spring Boot, remove “Cloud deployment is not included”, “Docker containerization”, and “Cloud deployment” from those sections.

give the sumple checkpoints version in the code to update the readme.md file small part 

Yes — keep it small and recruiter-friendly. Replace your current Development Checkpoints section with this:

# Development Checkpoints

ResumeIQ was developed incrementally, with each component tested before full integration.

| Phase | Component | Checkpoint |
|---|---|---|
| 1 | Spring Boot Setup | Backend starts successfully and accepts API requests |
| 2 | Resume Validation | PDF/DOCX files are validated before processing |
| 3 | Document Parsing | PDFBox and Apache POI extract readable resume text |
| 4 | Text Cleaning | Extracted resume text is normalized |
| 5 | FastAPI Setup | AI service starts and `/health` responds successfully |
| 6 | AI Extraction | `/analyze` returns structured resume and JD data |
| 7 | Skill Matching | Java identifies matched and missing required skills |
| 8 | Semantic Matching | `/match-requirements` returns resume evidence |
| 9 | Scoring | Java calculates Skill Match and ATS Compatibility |
| 10 | AI Generation | `/generate` returns suggestions and a cover letter |
| 11 | Frontend | Analysis results are displayed on the ResumeIQ dashboard |
| 12 | API Integration | Spring Boot communicates successfully with FastAPI |
| 13 | Error Handling | Invalid files, unreadable resumes and API failures are handled |
| 14 | Deployment | FastAPI and Spring Boot are deployed successfully |
| 15 | Production Integration | Deployed Java service communicates with deployed FastAPI |
| 16 | End-to-End Test | Resume + JD produces the complete analysis report |

### Final Checkpoint

```text
Resume Upload
     ↓
Validation & Parsing
     ↓
Text Cleaning
     ↓
AI Resume + JD Analysis
     ↓
Java Skill Matching
     ↓
Semantic Evidence Matching
     ↓
Java Scoring
     ↓
AI Suggestions + Cover Letter
     ↓
ResumeIQ Dashboard
     ↓
End-to-End Analysis ✓

This is much better for the README than the huge checkpoint list: it shows the **Spring Boot + FastAPI + AI + deployment lifecycle** without making the README unnecessarily long. Your current README already explains the individual stages in detail elsewhere. :contentReference[oaicite:0]{index=0}
can you generate a complete readme file with the deployment link highlight and checkpoints urls like https://ai-resume-analyzer-1-g2l2.onrender.com/docs
etc
the md file should professional and look beautifull using html css

Done — I generated the complete professional README.md based on your existing project documentation, while keeping the architecture and technical details consistent with your project.

It now includes a prominent Live Deployment section, Java + Python production URLs, /health, /docs, /openapi.json, all POST endpoints, concise development checkpoints, deployment checkpoints, Docker/Render details, architecture diagrams, screenshots, scoring methodology, security, limitations, future enhancements, and author section.

One small note: GitHub README files support HTML formatting, but GitHub strips custom <style> CSS for security. So I used GitHub-compatible HTML, tables, badges, callouts, Mermaid, and Markdown to make it look polished without relying on CSS that GitHub would remove.

Download

Download the complete professional README.md

You can rename it to README.md and replace the existing file in the repository. 

README_professional.md
Document

Library
/
README_professional.md
ResumeIQ --- AI Resume Analyzer
<p align="center">

<strong>{=html}AI-powered resume analysis with deterministic Java
scoring, semantic requirement matching, targeted suggestions, and
cover-letter generation.</strong>{=html}

</p>
<p align="center">

<img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java 21">{=html}
<img src="https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot">{=html}
<img src="https://img.shields.io/badge/Python-FastAPI-009688?style=for-the-badge&logo=fastapi&logoColor=white" alt="FastAPI">{=html}
<img src="https://img.shields.io/badge/AI-Groq-F55036?style=for-the-badge" alt="Groq">{=html}
<img src="https://img.shields.io/badge/Frontend-HTML%20%7C%20CSS%20%7C%20JS-2563EB?style=for-the-badge" alt="Frontend">{=html}

</p>

::: {align="center"}

🚀 Live Deployment

ResumeIQ is live and deployed as two independent services.

<a href="https://ai-resume-analyzer-1-g2l2.onrender.com/">{=html}<b>{=html}🌐
OPEN RESUMEIQ</b>{=html}</a>{=html}   •  
<a href="https://ai-resume-analyzer-uazs.onrender.com/docs">{=html}<b>{=html}🐍
FASTAPI SWAGGER</b>{=html}</a>{=html}   •  
<a href="https://github.com/ALDRIN1704/AI-Resume-Analyzer">{=html}<b>{=html}💻
SOURCE CODE</b>{=html}</a>{=html}
:::

<br>{=html}

<table>
<tr>
<th>

Checkpoint

</th>
<th>

URL

</th>
<th>

Purpose

</th>
</tr>
<tr>
<td>

<b>{=html}🌐 ResumeIQ Web App</b>{=html}

</td>
<td>

<a href="https://ai-resume-analyzer-1-g2l2.onrender.com/">{=html}Open
Application ↗</a>{=html}

</td>
<td>

Spring Boot + frontend

</td>
</tr>
<tr>
<td>

<b>{=html}☕ Spring Boot Analysis API</b>{=html}

</td>
<td>

<code>{=html}POST
https://ai-resume-analyzer-1-g2l2.onrender.com/api/analyze`</code>`{=html}

</td>
<td>

Complete analysis pipeline

</td>
</tr>
<tr>
<td>

<b>{=html}💚 FastAPI Health</b>{=html}

</td>
<td>

<a href="https://ai-resume-analyzer-uazs.onrender.com/health">{=html}Check
Health ↗</a>{=html}

</td>
<td>

Python service availability

</td>
</tr>
<tr>
<td>

<b>{=html}📘 FastAPI Swagger</b>{=html}

</td>
<td>

<a href="https://ai-resume-analyzer-uazs.onrender.com/docs">{=html}Open
Swagger ↗</a>{=html}

</td>
<td>

Interactive API documentation

</td>
</tr>
<tr>
<td>

<b>{=html}📄 FastAPI OpenAPI</b>{=html}

</td>
<td>

<a href="https://ai-resume-analyzer-uazs.onrender.com/openapi.json">{=html}Open
Specification ↗</a>{=html}

</td>
<td>

Machine-readable API specification

</td>
</tr>
</table>

[!NOTE] The POST endpoints require request bodies. Test them using
the ResumeIQ UI, Postman, or FastAPI Swagger rather than opening them
directly in a browser.

Overview

ResumeIQ is a full-stack AI Resume Analyzer that compares a
candidate's resume against a target job description.

The application accepts a PDF or DOCX resume and a job
description, extracts structured information from both, identifies
matched and missing skills, maps job requirements to evidence found in
the resume, calculates deterministic compatibility scores, generates
targeted resume-improvement suggestions, and produces a tailored cover
letter.

The system uses a hybrid architecture:

Java / Spring Boot handles document processing, orchestration,
deterministic matching, scoring, and the web application.
Python / FastAPI provides the AI/NLP service.
Groq-hosted LLM inference performs structured extraction,
semantic requirement analysis, suggestions, and grounded
cover-letter generation.
HTML, CSS, and JavaScript provide the responsive dashboard.

The scoring logic is intentionally kept outside the LLM so the AI does
not arbitrarily invent the final score.

Application Preview
<p align="center">

<img src="docs/images/dashboard.png" width="900" alt="ResumeIQ Dashboard">{=html}

</p>

The user uploads a resume and provides the job description they want to
target.

<p align="center">

<img src="docs/images/analysis-report.png" width="900" alt="ResumeIQ Analysis Report">{=html}

</p>

The generated report displays the Skill Match and ATS
Compatibility independently.

Key Features

Feature Description

Resume Upload Supports PDF and DOCX resumes up to
10 MB

Resume Parsing Apache PDFBox for PDF and Apache
POI for DOCX

Text Cleaning Normalizes extracted text while
preserving technical terms

AI Resume Extraction Extracts skills, experience,
projects, education, achievements,
research, leadership and activities

Job Description Analysis Extracts required skills, preferred
skills and categorized requirements

Skill Matching Deterministic comparison of
required JD skills against resume
skills

Semantic Requirement Matching Maps non-skill requirements to
actual resume evidence

ATS Compatibility Weighted deterministic
compatibility score calculated in
Java

Evidence Mapping Shows why a requirement was
considered matched

Gap Detection Identifies required skills or
requirements not supported by the
resume

Improvement Suggestions Generates targeted actions based on
the JD and resume gaps

Cover Letter Generation Creates a tailored cover letter
using resume-grounded information

Responsive Dashboard Presents results in a clean web
interface

System Architecture
flowchart TD
    A["Resume PDF / DOCX"] --> B["Spring Boot Backend"]
    JD["Job Description"] --> B

    B --> C["Document Parser"]
    C --> C1["Apache PDFBox"]
    C --> C2["Apache POI"]

    C1 --> D["Text Cleaning"]
    C2 --> D

    D --> E["FastAPI AI Service"]
    JD --> E

    E --> F["Structured Resume Analysis"]
    E --> G["Structured JD Analysis"]

    F --> H["Java Matching Engine"]
    G --> H

    H --> I["Skill Match"]
    H --> J["Semantic Evidence Matching"]

    I --> K["Java Scoring Engine"]
    J --> K

    K --> L["ATS Compatibility"]

    L --> M["AI Suggestions"]
    M --> N["Tailored Cover Letter"]

    I --> O["ResumeIQ Dashboard"]
    J --> O
    L --> O
    M --> O
    N --> O
Processing Pipeline
Resume PDF / DOCX + Job Description
                  │
                  ▼
          Spring Boot Backend
                  │
                  ▼
       Resume File Validation
                  │
          ┌───────┴───────┐
          ▼               ▼
       PDFBox         Apache POI
        PDF              DOCX
          └───────┬───────┘
                  ▼
           Extracted Text
                  │
                  ▼
           Text Cleaning
                  │
                  ▼
        FastAPI AI Service
                  │
          ┌───────┴────────┐
          ▼                ▼
    Resume Analysis     JD Analysis
          │                │
          └───────┬────────┘
                  ▼
          Matching Engine
          ┌───────┴─────────┐
          ▼                 ▼
     Skill Matching    Evidence Matching
          │                 │
          └────────┬────────┘
                   ▼
          Java Scoring Engine
                   │
          ┌────────┴────────┐
          ▼                 ▼
      Skill Match     ATS Compatibility
                   │
                   ▼
          AI Recommendations
                   │
                   ▼
           Tailored Cover Letter
                   │
                   ▼
            Web Dashboard
Technology Stack
Backend
Java 21
Spring Boot 4.1.1
Spring Web MVC
Jakarta Validation
Maven
Document Processing
Apache PDFBox 3.0.5
Apache POI OOXML 5.4.1
AI / NLP Service
Python
FastAPI
Pydantic
Uvicorn
Groq API
openai/gpt-oss-120b
Frontend
HTML5
CSS3
Vanilla JavaScript
Fetch API
Deployment
Docker
Render
Environment Variables
Development & Testing
Eclipse IDE
PyCharm
Postman
FastAPI Swagger / OpenAPI
Git
GitHub
Development Approach

The application was developed incrementally using a phase-by-phase
checkpoint approach.

Each major component was implemented and tested independently before
being connected to the complete pipeline.

Phase 1 --- Resume Parsing

The first phase focused entirely on reliable document extraction.

PDF

PDF resumes are processed using:

Apache PDFBox
DOCX

DOCX resumes are processed using:

Apache POI
Checkpoint
Resume
   ↓
File validation
   ↓
PDF / DOCX parser
   ↓
Readable resume text

Image-based or scanned PDFs without extractable text return a clear
validation error instead of silently continuing with empty content.

Phase 2 --- Resume Text Cleaning

Extracted text is normalized before being passed to the AI service.

The cleaning stage handles:

Windows and Unix line endings
Non-breaking spaces
Repeated spaces
Excessive blank lines
Leading and trailing whitespace

Technical punctuation is deliberately preserved because terms such as:

C++
C#
.NET
Node.js
CGPA
dates

may be important resume information.

Checkpoint
Raw Resume Text
       ↓
TextCleaningService
       ↓
Clean Resume Text
Phase 3 --- Structured AI Extraction

The cleaned resume and job description are sent from Spring Boot to the
FastAPI service.

Endpoint
POST /analyze

The AI converts unstructured text into predictable structured JSON.

Resume information
{
  "skills": [],
  "experience": [],
  "projects": [],
  "education": [],
  "certifications": [],
  "achievements": [],
  "research": [],
  "leadership": [],
  "activities": []
}
Job information

The job description is separated into:

Required Skills
Preferred Skills
Requirements

Each requirement also contains:

Category
Priority

Supported categories include:

SKILL
EXPERIENCE
PROJECT
EDUCATION
CERTIFICATION
RESEARCH
ACHIEVEMENT
LEADERSHIP
ACTIVITY
OTHER

Requirement priorities are:

REQUIRED
PREFERRED
OPTIONAL
Checkpoint
<p align="center">

<img src="docs/images/fastapi-swagger.png" width="850" alt="FastAPI Swagger API">{=html}

</p>
Phase 4A --- Deterministic Skill Matching

Required technical skills are matched in Java rather than asking the LLM
to assign a score.

For every required skill:

JD Required Skill
       ↓
Normalize
       ↓
Compare against Resume Skills
       ↓
Matched / Missing
Skill Match Formula
Skill Match % =
Matched Required Skills
──────────────────────── × 100
Total Required Skills

Example:

Matched Required Skills = 7
Total Required Skills   = 13

Skill Match = 7 / 13 × 100
            = 53.85%

The dashboard displays both the count and percentage.

Phase 4B --- Semantic Requirement Matching

Simple string comparison is insufficient for requirements such as:

Experience building backend applications
Experience working in Agile teams
Relevant project experience
Degree in Information Technology

For these requirements, the FastAPI AI service performs semantic
evidence matching.

Endpoint
POST /match-requirements

The service determines:

{
  "requirement": "Backend or full-stack software projects",
  "category": "PROJECT",
  "priority": "REQUIRED",
  "matched": true,
  "evidence": "Resume evidence supporting the requirement"
}

The model is instructed to use only information actually present in the
resume.

If evidence cannot be found:

{
  "matched": false,
  "evidence": ""
}

This allows the dashboard to explain why a requirement was matched
rather than returning only a score.

Phase 5 --- ATS Compatibility Scoring

The final compatibility score is calculated deterministically in Java.

The LLM does not choose the score.

Requirements receive weights according to their importance:

Priority Weight

Required 3
Preferred 2
Optional 1

Formula
ATS Compatibility =
Earned Weighted Points
────────────────────── × 100
Total Weighted Points

Example:

Earned Points = 15
Total Points  = 34

ATS Compatibility = 15 / 34 × 100
                  = 44.12%
Why two scores?

ResumeIQ deliberately separates:

Skill Match

from:

ATS Compatibility

Skill Match measures required technical skill coverage.

ATS Compatibility evaluates broader weighted job requirements
including skills, experience, education, projects and other relevant
criteria.

Note: ResumeIQ's ATS Compatibility score is an application-defined
compatibility metric. It does not claim to reproduce the internal
ranking algorithm of any commercial Applicant Tracking System.

Phase 6 --- AI Suggestions and Cover Letter

Once deterministic matching and scoring are complete, the results are
passed back to the AI service.

Endpoint
POST /generate

Two outputs are generated independently.

Improvement Suggestions

Suggestions focus on:

missing requirements
weakly communicated experience
JD-specific keywords
project descriptions
technical evidence
resume clarity

The system is instructed not to recommend fabricating qualifications.

For genuinely missing skills, the candidate may be advised to learn the
technology or include it only if they actually have that experience.

Tailored Cover Letter

The cover letter is generated from information extracted from the
resume.

It is designed to:

highlight relevant experience
emphasize matching projects
reference relevant technical skills
align with the target role
avoid intentionally inventing missing qualifications
Phase 7 --- Frontend Dashboard

The final phase connected the complete backend pipeline to a responsive
frontend served directly by Spring Boot.

<p align="center">

<img src="docs/images/analysis-report.png" width="900" alt="Skill and ATS Analysis">{=html}

</p>

The results follow a clear hierarchy:

Scores
  ↓
Skills
  ↓
Requirement Evidence
  ↓
Improvement Suggestions
  ↓
Tailored Cover Letter
Requirement Evidence
<p align="center">

<img src="docs/images/requirement-analysis.png" width="900" alt="Requirement Analysis">{=html}

</p>

Instead of displaying only a percentage, ResumeIQ shows the resume
evidence associated with job requirements.

Improvement Suggestions
<p align="center">

<img src="docs/images/suggestions.png" width="900" alt="Resume Improvement Suggestions">{=html}

</p>
Tailored Cover Letter
<p align="center">

<img src="docs/images/cover-letter.png" width="900" alt="Tailored Cover Letter">{=html}

</p>

The generated letter can be copied directly from the dashboard.

API Design

The application consists of two communicating services.

Spring Boot API
Analyze Resume
POST /api/analyze

Content type:

multipart/form-data

Parameters:

Parameter Type Description

resume File PDF or DOCX resume
jobDescription Text Target job description

Example using Postman:

<p align="center">

<img src="docs/images/postman-api.png" width="900" alt="Spring Boot API tested using Postman">{=html}

</p>

The Spring Boot API orchestrates the complete pipeline and returns the
final analysis result.

FastAPI AI Service

The Python service exposes the following endpoints:

Method Endpoint Purpose

GET /health AI service health check
POST /analyze Resume and JD structured extraction
POST /match-requirements Semantic evidence matching
POST /generate Suggestions and cover-letter generation

FastAPI automatically provides Swagger/OpenAPI documentation.

http://localhost:8000/docs
<p align="center">

<img src="docs/images/fastapi-swagger.png" width="900" alt="FastAPI Swagger Documentation">{=html}

</p>
Production Endpoint Checkpoints
Spring Boot / Java
Application
https://ai-resume-analyzer-1-g2l2.onrender.com/

POST /api/analyze
https://ai-resume-analyzer-1-g2l2.onrender.com/api/analyze
FastAPI / Python
GET /health
https://ai-resume-analyzer-uazs.onrender.com/health

GET /docs
https://ai-resume-analyzer-uazs.onrender.com/docs

GET /openapi.json
https://ai-resume-analyzer-uazs.onrender.com/openapi.json

POST /analyze
https://ai-resume-analyzer-uazs.onrender.com/analyze

POST /match-requirements
https://ai-resume-analyzer-uazs.onrender.com/match-requirements

POST /generate
https://ai-resume-analyzer-uazs.onrender.com/generate
Java ↔ Python Integration

Spring Boot communicates with FastAPI over HTTP.

Spring Boot
    │
    │ JSON / HTTP
    ▼
FastAPI
    │
    │ Groq API
    ▼
AI Model

Java uses HttpClient with HTTP/1.1 for communication with the Python
service.

Spring Boot : 8081
FastAPI     : 8000

The services communicate locally using:

http://127.0.0.1:8000
Project Structure
AI-Resume-Analyzer/
│
├── resume-analyzer/
│   │
│   ├── src/main/java/com/project/resume_analyzer/
│   │   │
│   │   ├── controller/
│   │   │   └── ResumeController.java
│   │   │
│   │   ├── dto/
│   │   │   ├── AIAnalysisRequest.java
│   │   │   ├── AIAnalysisResponse.java
│   │   │   ├── AnalysisResult.java
│   │   │   ├── ATSScoreResult.java
│   │   │   ├── GenerationResponse.java
│   │   │   ├── JobAnalysis.java
│   │   │   ├── JobRequirement.java
│   │   │   ├── MatchResult.java
│   │   │   ├── RequirementMatch.java
│   │   │   ├── RequirementMatchResponse.java
│   │   │   └── ResumeAnalysis.java
│   │   │
│   │   ├── service/
│   │   │   ├── AIService.java
│   │   │   ├── MatchingService.java
│   │   │   ├── ResumeParserService.java
│   │   │   ├── ScoringService.java
│   │   │   └── TextCleaningService.java
│   │   │
│   │   └── ResumeAnalyzerApplication.java
│   │
│   ├── src/main/resources/
│   │   ├── static/
│   │   │   ├── css/
│   │   │   ├── js/
│   │   │   └── index.html
│   │   │
│   │   └── application.properties
│   │
│   ├── Dockerfile
│   └── pom.xml
│
├── resume-ai-service/
│   ├── ai_service.py
│   ├── main.py
│   ├── models.py
│   ├── requirements.txt
│   └── .env
│
├── docs/
│   └── images/
│
├── .gitignore
└── README.md

.env, virtual environments and generated build directories should
not be committed to Git.

Running the Project Locally
Prerequisites

Install:

Java 21
Maven
Python 3
Git

A Groq API key is also required for AI inference.

1. Clone the Repository
git clone https://github.com/ALDRIN1704/AI-Resume-Analyzer.git
cd AI-Resume-Analyzer
2. Configure the Python AI Service

Move into the service:

cd resume-ai-service

Create a virtual environment:

python -m venv .venv
Windows
.venv\Scripts\activate

Install dependencies:

pip install fastapi uvicorn groq python-dotenv

Create:

.env

Add:

GROQ_API_KEY=your_groq_api_key

Never commit your .env file or API key.

Start FastAPI:

python -m uvicorn main:app --host 127.0.0.1 --port 8000

Expected output:

Uvicorn running on http://127.0.0.1:8000

Swagger documentation is available at:

http://localhost:8000/docs
3. Start Spring Boot

Open another terminal:

cd resume-analyzer

Run:

mvn spring-boot:run

Alternatively, run:

ResumeAnalyzerApplication.java

from Eclipse as a Spring Boot application.

Spring Boot starts on:

http://localhost:8081
4. Open ResumeIQ

Open:

http://localhost:8081/

Then:

Upload a PDF or DOCX resume.
Paste the target job description.
Click Analyze Resume.
Wait for the AI and matching pipeline to complete.
Review the generated report.
End-to-End Request Flow
sequenceDiagram
    actor User
    participant UI as ResumeIQ UI
    participant Java as Spring Boot
    participant Parser as Document Parser
    participant AI as FastAPI
    participant LLM as Groq LLM

    User->>UI: Upload Resume + Job Description
    UI->>Java: POST /api/analyze
    Java->>Parser: Extract Resume Text
    Parser-->>Java: Extracted Text
    Java->>Java: Clean Text

    Java->>AI: POST /analyze
    AI->>LLM: Structured Extraction
    LLM-->>AI: Resume + JD JSON
    AI-->>Java: Structured Analysis

    Java->>Java: Match Required Skills

    Java->>AI: POST /match-requirements
    AI->>LLM: Find Resume Evidence
    LLM-->>AI: Requirement Matches
    AI-->>Java: Evidence Results

    Java->>Java: Calculate Weighted Scores

    Java->>AI: POST /generate
    AI->>LLM: Generate Suggestions + Cover Letter
    LLM-->>AI: Generated Content
    AI-->>Java: Suggestions + Cover Letter

    Java-->>UI: Complete Analysis Result
    UI-->>User: Resume Match Report
Design Principles
Deterministic scoring

The LLM extracts and interprets information, but the final scores are
calculated using Java business logic.

This makes the scoring process easier to understand and reproduce.

Evidence before claims

Semantic requirement matches include supporting resume evidence wherever
possible.

No fabricated qualifications

Generated suggestions and cover letters are designed to rely on
resume-supported information rather than intentionally adding
qualifications that the candidate has not provided.

Job-dependent evaluation

Different jobs emphasize different requirements. ResumeIQ extracts
requirement priorities from the supplied JD rather than using one fixed
scoring template for every role.

Separate skill and compatibility scores

Technical skill coverage and broader job compatibility answer different
questions, so they are displayed independently.

Error Handling

The application handles common failures including:

Empty resume upload
Unsupported file formats
Empty job description
PDF without readable text
DOCX without readable text
AI service communication failure
Invalid AI response
API processing errors

Supported resume formats:

.pdf
.docx

Maximum upload size:

10 MB
Current Limitations

ResumeIQ is an engineering prototype and currently has several
limitations:

Scanned/image-only PDFs do not yet use OCR.
Semantic analysis depends on an external LLM service.
Skill matching currently relies primarily on normalized skill names.
Skill aliases and technology equivalence can be improved.
The application does not reproduce the proprietary scoring algorithm
of any commercial ATS.
Authentication and user accounts are not implemented.
Analysis history is not currently persisted.
Future Enhancements

Potential improvements include:

OCR support for scanned resumes
Skill synonym and alias normalization
Embedding-based semantic skill matching
Resume section quality analysis
Resume formatting checks
Configurable scoring weights
User authentication
Analysis history
Database persistence
Resume comparison across multiple job descriptions
Export analysis as PDF
CI/CD pipeline
Automated tests
Rate limiting and API resilience
Development Checkpoints

ResumeIQ was developed incrementally, with every major component tested
before full integration.

                     Phase Component             Checkpoint
                         1 Spring Boot Setup     Backend starts
                                                 successfully and
                                                 accepts API requests

                         2 Resume Validation     PDF/DOCX files are
                                                 validated before
                                                 processing

                         3 Document Parsing      PDFBox and Apache POI
                                                 extract readable resume
                                                 text

                         4 Text Cleaning         Resume text is
                                                 normalized while
                                                 preserving technical
                                                 terms

                         5 FastAPI Setup         AI service starts and
                                                 `/health` responds
                                                 successfully

                         6 AI Extraction         `/analyze` returns
                                                 structured resume and
                                                 JD data

                         7 Skill Matching        Java identifies matched
                                                 and missing required
                                                 skills

                         8 Semantic Matching     `/match-requirements`
                                                 returns grounded resume
                                                 evidence

                         9 Scoring               Java calculates Skill
                                                 Match and ATS
                                                 Compatibility

                        10 AI Generation         `/generate` returns
                                                 suggestions and a cover
                                                 letter

                        11 Frontend              Analysis results render
                                                 in the ResumeIQ
                                                 dashboard

                        12 API Integration       Spring Boot
                                                 communicates
                                                 successfully with
                                                 FastAPI

                        13 Error Handling        Invalid files,
                                                 unreadable resumes and
                                                 API failures are
                                                 handled

                        14 Deployment            FastAPI and Spring Boot
                                                 are deployed
                                                 independently

                        15 Production            Deployed Java
                           Integration           communicates with
                                                 deployed FastAPI

                        16 End-to-End Test       Resume + JD produces
                                                 the complete analysis
                                                 report
Final Checkpoint
Resume Upload
     ↓
Validation & Parsing
     ↓
Text Cleaning
     ↓
AI Resume + JD Analysis
     ↓
Java Skill Matching
     ↓
Semantic Evidence Matching
     ↓
Java Scoring
     ↓
AI Suggestions + Cover Letter
     ↓
ResumeIQ Dashboard
     ↓
End-to-End Analysis ✓
API Testing

The APIs were tested independently during development before full
integration.

Spring Boot

Tested using Postman with multipart form data:

resume         → File
jobDescription → Text
<p align="center">

<img src="docs/images/postman-api.png" width="900" alt="Postman API Testing">{=html}

</p>
FastAPI

The Python endpoints were independently verified through
Swagger/OpenAPI.

<p align="center">

<img src="docs/images/fastapi-swagger.png" width="900" alt="FastAPI Endpoint Testing">{=html}

</p>

This checkpoint-based development process helped isolate document
parsing, HTTP communication, AI response validation, semantic matching
and scoring before integrating the complete application.

Backend Development
Spring Boot
<p align="center">

<img src="docs/images/spring-boot.png" width="900" alt="Spring Boot Backend">{=html}

</p>
FastAPI AI Service
<p align="center">

<img src="docs/images/python-service.png" width="900" alt="Python FastAPI AI Service">{=html}

</p>
Security

Sensitive configuration is stored using environment variables.

The following files/directories should be excluded from Git:

.env
*.env
.venv/
venv/
__pycache__/
*.pyc
target/

Never commit API keys directly into source code.

What I Learned

Building ResumeIQ involved integrating multiple areas of software
engineering:

Spring Boot REST API development
Multipart file handling
PDF and DOCX document processing
Java service-layer architecture
Java records and DTO design
Java HTTP client communication
Python FastAPI development
Pydantic request/response validation
LLM structured JSON extraction
Prompt design for controlled AI outputs
Deterministic scoring algorithms
Semantic requirement matching
Error handling across services
API testing using Postman and Swagger
Responsive frontend development
Docker and cloud deployment
Git and GitHub version control

A major design focus was separating AI language understanding from
deterministic application logic rather than allowing the LLM to
control the complete evaluation.

Author

Aldrin Lijo E M

B.Tech --- Information Technology
St. Joseph's Institute of Technology, Chennai

GitHub: @ALDRIN1704

<p align="center">

<strong>{=html}ResumeIQ</strong>{=html}<br>{=html} Built with
Java, Spring Boot, FastAPI and AI.

</p>
