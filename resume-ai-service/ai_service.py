import os
import json

from dotenv import load_dotenv
from groq import Groq


# --------------------------------------------------
# Load environment variables
# --------------------------------------------------

load_dotenv()

GROQ_API_KEY = os.getenv("GROQ_API_KEY")

if not GROQ_API_KEY:
    raise RuntimeError(
        "GROQ_API_KEY is not set. "
        "Please add GROQ_API_KEY to your .env file."
    )


# --------------------------------------------------
# Groq client
# --------------------------------------------------

client = Groq(api_key=GROQ_API_KEY)


# --------------------------------------------------
# Model
# --------------------------------------------------

MODEL = "openai/gpt-oss-120b"

# --------------------------------------------------
# System Prompt
# --------------------------------------------------

SYSTEM_PROMPT = """
You are a resume and job description analysis engine.

Your job is ONLY to extract structured information from:
1. A candidate's resume
2. A job description

IMPORTANT RULES:

- Extract information ONLY when it is explicitly present in the
  provided resume or job description.
- Do NOT invent skills.
- Do NOT infer skills.
- Do NOT assume experience.
- Do NOT assume qualifications.
- Do NOT calculate a resume score.
- Do NOT rank the candidate.
- Do NOT add recommendations.
- Do NOT add explanations.
- Return ONLY valid JSON.
- Do NOT use Markdown.
- Do NOT wrap the JSON inside ```json blocks.

Return EXACTLY this structure:

{
  "resume": {
    "skills": [],
    "experience": [],
    "projects": [],
    "education": [],
    "certifications": [],
    "achievements": [],
    "research": [],
    "leadership": [],
    "activities": []
  },
  "job": {
    "requiredSkills": [],
    "preferredSkills": [],
    "requirements": []
  }
}

For job requirements, every requirement must have:

{
  "name": "",
  "category": "",
  "priority": ""
}

Allowed category values:

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

Allowed priority values:

REQUIRED
PREFERRED
OPTIONAL

Interpret requirements as follows:

REQUIRED:
The job description explicitly states that the candidate must have
the requirement.

PREFERRED:
The job description explicitly describes the requirement as preferred,
good to have, desirable, or similar.

OPTIONAL:
The requirement is mentioned but is not clearly required or preferred.

For the resume:

skills:
Explicit technical and professional skills mentioned in the resume.

experience:
Explicit work/internship experience.

projects:
Explicit projects mentioned in the resume.

education:
Degrees, universities, colleges, courses, or educational qualifications.

certifications:
Explicit certifications.

achievements:
Awards, prizes, rankings, competitions, or other explicit achievements.

research:
Research papers, publications, patents, or research work.

leadership:
Leadership positions or responsibilities.

activities:
Other extracurricular or professional activities.

If a section contains no information, return an empty array.

For the job:

requiredSkills:
Skills explicitly required by the job description.

preferredSkills:
Skills explicitly described as preferred, desirable,
good-to-have, or similar.

requirements:
All explicitly stated job requirements represented as structured
objects.

Do not include information that is not supported by the input text.
"""


# --------------------------------------------------
# Analyze Resume and Job Description
# --------------------------------------------------

def analyze_resume_and_job(
    resume_text: str,
    job_description: str
) -> dict:

    """
    Analyze a resume against a job description.

    Parameters
    ----------
    resume_text : str
        Candidate resume text.

    job_description : str
        Job description text.

    Returns
    -------
    dict
        Structured JSON-compatible dictionary.
    """

    # --------------------------------------------------
    # Validate input
    # --------------------------------------------------

    if not resume_text or not resume_text.strip():
        raise ValueError("Resume text cannot be empty.")

    if not job_description or not job_description.strip():
        raise ValueError("Job description cannot be empty.")


    # --------------------------------------------------
    # User prompt
    # --------------------------------------------------

    user_prompt = f"""
RESUME TEXT:
--------------------
{resume_text}
--------------------

JOB DESCRIPTION:
--------------------
{job_description}
--------------------

Extract the structured information according to the rules
defined in the system prompt.

Return ONLY valid JSON.
"""


    # --------------------------------------------------
    # Call Groq
    # --------------------------------------------------

    try:

        completion = client.chat.completions.create(
            model=MODEL,

            messages=[
                {
                    "role": "system",
                    "content": SYSTEM_PROMPT
                },
                {
                    "role": "user",
                    "content": user_prompt
                }
            ],

            temperature=0.2,

            response_format={
                "type": "json_object"
            }
        )

    except Exception as e:

        raise RuntimeError(
            f"Groq API request failed: {str(e)}"
        )


    # --------------------------------------------------
    # Get response content
    # --------------------------------------------------

    try:

        raw_content = completion.choices[0].message.content

    except Exception as e:

        raise RuntimeError(
            f"Unable to read Groq response: {str(e)}"
        )


    # --------------------------------------------------
    # Validate response content
    # --------------------------------------------------

    if not raw_content:

        raise RuntimeError(
            "Groq returned an empty response."
        )


    # --------------------------------------------------
    # Convert JSON string → Python dictionary
    # --------------------------------------------------

    try:

        result = json.loads(raw_content)

    except json.JSONDecodeError as e:

        raise RuntimeError(
            f"Groq returned invalid JSON: {str(e)}"
        )


    # --------------------------------------------------
    # Basic structure validation
    # --------------------------------------------------

    if not isinstance(result, dict):

        raise RuntimeError(
            "Groq response is not a JSON object."
        )


    if "resume" not in result:

        raise RuntimeError(
            "Groq response is missing 'resume'."
        )


    if "job" not in result:

        raise RuntimeError(
            "Groq response is missing 'job'."
        )


    # --------------------------------------------------
    # Return result
    # --------------------------------------------------

    return result


def match_requirements(
    resume: dict,
    requirements: list
) -> dict:

    if not requirements:
        return {"matches": []}

    prompt = f"""
You are an evidence matching engine.

Determine whether each job requirement is supported by the
candidate's resume.

RESUME:
{json.dumps(resume, indent=2)}

JOB REQUIREMENTS:
{json.dumps(requirements, indent=2)}

RULES:

1. Evaluate every supplied requirement.
2. Use ONLY information explicitly supported by the resume.
3. Never invent experience, skills, certifications, or qualifications.
4. A match requires meaningful resume evidence.
5. Do not assume a technology automatically proves another skill.

Example:
Spring Boot alone does NOT prove REST API experience unless the
resume explicitly describes REST APIs, API development, endpoints,
web services, or equivalent evidence.

6. Semantic evidence is allowed.

Example:
Requirement:
"Experience building backend projects"

Resume:
"Built a full-stack Task Manager using Spring Boot and MongoDB"

This can be considered supporting evidence for backend project
experience.

7. If matched is true, evidence must contain concise evidence taken
from or faithfully summarized from the resume.

8. If matched is false, evidence must be an empty string.

9. Preserve the original requirement category and priority.

Return ONLY valid JSON.

Return exactly:

{{
  "matches": [
    {{
      "requirement": "requirement name",
      "category": "category",
      "priority": "priority",
      "matched": true,
      "evidence": "supporting resume evidence"
    }}
  ]
}}
"""

    try:

        completion = client.chat.completions.create(
            model=MODEL,
            messages=[
                {
                    "role": "system",
                    "content":
                        "You are a strict resume evidence matching "
                        "engine. Return only valid JSON and never "
                        "invent candidate evidence."
                },
                {
                    "role": "user",
                    "content": prompt
                }
            ],
            temperature=0.1,
            response_format={
                "type": "json_object"
            }
        )

        raw_content = completion.choices[0].message.content

        if not raw_content:
            raise RuntimeError(
                "Groq returned an empty requirement matching response."
            )

        result = json.loads(raw_content)

        if "matches" not in result:
            raise RuntimeError(
                "Groq response is missing 'matches'."
            )

        return result

    except json.JSONDecodeError as e:

        raise RuntimeError(
            f"Groq returned invalid requirement JSON: {str(e)}"
        )

    except Exception as e:

        raise RuntimeError(
            f"Requirement matching failed: {str(e)}"
        )



def generate_suggestions(
    resume: dict,
    job: dict,
    matched_skills: list,
    missing_skills: list,
    requirement_matches: list,
    ats_score: float
) -> list:

    prompt = f"""
You are helping a candidate improve a resume for a specific job.

RESUME:
{json.dumps(resume, indent=2)}

JOB:
{json.dumps(job, indent=2)}

MATCHED SKILLS:
{json.dumps(matched_skills)}

MISSING REQUIRED SKILLS:
{json.dumps(missing_skills)}

REQUIREMENT MATCHES:
{json.dumps(requirement_matches, indent=2)}

ATS COMPATIBILITY SCORE:
{ats_score}

Generate 3 to 5 concise, practical resume improvement suggestions.

STRICT RULES:

1. Never tell the candidate to claim a skill or experience they
   do not have.

2. Never invent qualifications.

3. Distinguish between:
   - information already present but poorly communicated
   - genuinely missing requirements

4. If a requirement is missing, suggest learning it or adding it
   only if the candidate genuinely has that experience.

5. Prioritize improvements relevant to the supplied job description.

6. Suggestions should be actionable.

7. Do not calculate another score.

Return ONLY valid JSON:

{{
  "suggestions": [
    "Suggestion 1",
    "Suggestion 2"
  ]
}}
"""

    try:

        completion = client.chat.completions.create(
            model=MODEL,
            messages=[
                {
                    "role": "system",
                    "content":
                        "You generate truthful, job-specific resume "
                        "improvement suggestions. Never invent "
                        "candidate qualifications."
                },
                {
                    "role": "user",
                    "content": prompt
                }
            ],
            temperature=0.3,
            response_format={
                "type": "json_object"
            }
        )

        content = completion.choices[0].message.content

        if not content:
            raise RuntimeError(
                "Groq returned empty suggestions."
            )

        result = json.loads(content)

        suggestions = result.get("suggestions")

        if not isinstance(suggestions, list):
            raise RuntimeError(
                "Invalid suggestions response."
            )

        return suggestions

    except Exception as error:

        raise RuntimeError(
            f"Suggestion generation failed: {str(error)}"
        )



def generate_cover_letter(
            resume: dict,
            job: dict,
            matched_skills: list,
            requirement_matches: list
    ) -> str:

        prompt = f"""
    Write a concise professional cover letter based ONLY on the
    candidate information provided below.

    RESUME:
    {json.dumps(resume, indent=2)}

    JOB:
    {json.dumps(job, indent=2)}

    MATCHED SKILLS:
    {json.dumps(matched_skills)}

    REQUIREMENT MATCHES:
    {json.dumps(requirement_matches, indent=2)}

    RULES:

    1. Never invent candidate qualifications.
    2. Never claim a missing skill.
    3. Never invent company information.
    4. Use only resume-supported facts.
    5. Emphasize relevant genuine skills, projects, education and
       experience.
    6. Keep the letter approximately 200 to 300 words.
    7. Use a professional but natural tone.
    8. Do not include Markdown.
    9. Do not mention ATS scores.
    10. Do not mention that AI generated the letter.

    Return ONLY valid JSON:

    {{
      "coverLetter": "..."
    }}
    """

        try:

            completion = client.chat.completions.create(
                model=MODEL,
                messages=[
                    {
                        "role": "system",
                        "content":
                            "You write truthful professional cover "
                            "letters using only supplied candidate facts."
                    },
                    {
                        "role": "user",
                        "content": prompt
                    }
                ],
                temperature=0.4,
                response_format={
                    "type": "json_object"
                }
            )

            content = completion.choices[0].message.content

            if not content:
                raise RuntimeError(
                    "Groq returned an empty cover letter."
                )

            result = json.loads(content)

            cover_letter = result.get("coverLetter")

            if not cover_letter:
                raise RuntimeError(
                    "Invalid cover letter response."
                )

            return cover_letter

        except Exception as error:

            raise RuntimeError(
                f"Cover letter generation failed: {str(error)}"
            )

def generate_resume_content(
    resume: dict,
    job: dict,
    matched_skills: list,
    missing_skills: list,
    requirement_matches: list,
    ats_score: float
) -> dict:

    suggestions = generate_suggestions(
        resume,
        job,
        matched_skills,
        missing_skills,
        requirement_matches,
        ats_score
    )

    cover_letter = generate_cover_letter(
        resume,
        job,
        matched_skills,
        requirement_matches
    )

    return {
        "suggestions": suggestions,
        "coverLetter": cover_letter
    }