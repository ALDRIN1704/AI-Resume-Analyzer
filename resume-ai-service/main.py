from fastapi import FastAPI, HTTPException, Request

from ai_service import (
    analyze_resume_and_job,
    match_requirements,
    generate_resume_content
)
from models import (
    AnalysisRequest,
    AIAnalysisResponse,
    RequirementMatchRequest,
    RequirementMatchResponse,
    GenerationRequest,
    GenerationResponse
)


app = FastAPI(
    title="Resume Analyzer AI Service",
    version="1.0.0"
)


@app.middleware("http")
async def debug_request(request: Request, call_next):
    body = await request.body()

    print("\n===== INCOMING REQUEST =====")
    print("METHOD:", request.method)
    print("URL:", request.url)
    print("CONTENT-TYPE:", request.headers.get("content-type"))
    print("CONTENT-LENGTH:", request.headers.get("content-length"))
    print("BODY LENGTH:", len(body))
    print("BODY:", body[:500])
    print("============================\n")

    response = await call_next(request)
    return response


@app.get("/health")
def health():
    return {
        "status": "running",
        "service": "Resume Analyzer AI Service"
    }


@app.post(
    "/analyze",
    response_model=AIAnalysisResponse
)
def analyze(request: AnalysisRequest):

    try:
        return analyze_resume_and_job(
            request.resumeText,
            request.jobDescription
        )

    except ValueError as error:
        raise HTTPException(
            status_code=400,
            detail=str(error)
        )

    except RuntimeError as error:
        raise HTTPException(
            status_code=500,
            detail=str(error)
        )


@app.post(
    "/match-requirements",
    response_model=RequirementMatchResponse
)
def match_job_requirements(
    request: RequirementMatchRequest
):

    try:
        resume_data = request.resume.model_dump()

        requirements_data = [
            requirement.model_dump()
            for requirement in request.requirements
        ]

        result = match_requirements(
            resume_data,
            requirements_data
        )

        return result

    except ValueError as error:
        raise HTTPException(
            status_code=400,
            detail=str(error)
        )

    except RuntimeError as error:
        raise HTTPException(
            status_code=500,
            detail=str(error)
        )

@app.post(
    "/generate",
    response_model=GenerationResponse
)
def generate_content(
    request: GenerationRequest
):

    try:

        result = generate_resume_content(
            request.resume.model_dump(),
            request.job.model_dump(),
            request.matchedSkills,
            request.missingSkills,
            [
                match.model_dump()
                for match in request.requirementMatches
            ],
            request.atsScore
        )

        return result

    except RuntimeError as error:

        raise HTTPException(
            status_code=500,
            detail=str(error)
        )