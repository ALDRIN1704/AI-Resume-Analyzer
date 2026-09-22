from typing import List
from pydantic import BaseModel


class AnalysisRequest(BaseModel):
    resumeText: str
    jobDescription: str


class ResumeAnalysis(BaseModel):
    skills: List[str]
    experience: List[str]
    projects: List[str]
    education: List[str]
    certifications: List[str]
    achievements: List[str]
    research: List[str]
    leadership: List[str]
    activities: List[str]


class JobRequirement(BaseModel):
    name: str
    category: str
    priority: str


class JobAnalysis(BaseModel):
    requiredSkills: List[str]
    preferredSkills: List[str]
    requirements: List[JobRequirement]


class AIAnalysisResponse(BaseModel):
    resume: ResumeAnalysis
    job: JobAnalysis


class RequirementMatchRequest(BaseModel):
    resume: ResumeAnalysis
    requirements: List[JobRequirement]


class RequirementMatch(BaseModel):
    requirement: str
    category: str
    priority: str
    matched: bool
    evidence: str


class RequirementMatchResponse(BaseModel):
    matches: List[RequirementMatch]


class GenerationRequest(BaseModel):
    resume: ResumeAnalysis
    job: JobAnalysis
    matchedSkills: List[str]
    missingSkills: List[str]
    requirementMatches: List[RequirementMatch]
    atsScore: float


class GenerationResponse(BaseModel):
    suggestions: List[str]
    coverLetter: str