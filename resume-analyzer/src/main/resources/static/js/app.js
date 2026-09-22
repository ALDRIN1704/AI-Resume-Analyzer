const resumeInput = document.getElementById("resumeInput");
const uploadArea = document.getElementById("uploadArea");
const selectedFile = document.getElementById("selectedFile");
const fileName = document.getElementById("fileName");
const fileSize = document.getElementById("fileSize");
const removeFile = document.getElementById("removeFile");

const jobDescription = document.getElementById("jobDescription");
const characterCount = document.getElementById("characterCount");

const analyzeButton = document.getElementById("analyzeButton");
const buttonText = document.getElementById("buttonText");
const loadingSpinner = document.getElementById("loadingSpinner");
const errorMessage = document.getElementById("errorMessage");

const resultsSection = document.getElementById("resultsSection");

const skillScore = document.getElementById("skillScore");
const skillFraction = document.getElementById("skillFraction");
const skillProgress = document.getElementById("skillProgress");

const atsScore = document.getElementById("atsScore");
const atsFraction = document.getElementById("atsFraction");
const atsProgress = document.getElementById("atsProgress");

const matchedSkills = document.getElementById("matchedSkills");
const missingSkills = document.getElementById("missingSkills");

const requirementsList = document.getElementById("requirementsList");
const suggestionsList = document.getElementById("suggestionsList");
const coverLetter = document.getElementById("coverLetter");

const copyButton = document.getElementById("copyButton");
const newAnalysisButton = document.getElementById("newAnalysisButton");

let currentFile = null;


/* =========================
   FILE SELECTION
========================= */

resumeInput.addEventListener("change", () => {
    const file = resumeInput.files[0];

    if (file) {
        handleFile(file);
    }
});


function handleFile(file) {

    hideError();

    const validExtensions = [
        "pdf",
        "docx"
    ];

    const extension = file.name
        .split(".")
        .pop()
        .toLowerCase();

    if (!validExtensions.includes(extension)) {
        showError(
            "Please upload a PDF or DOCX resume."
        );

        resetFile();
        return;
    }

    const maxSize = 10 * 1024 * 1024;

    if (file.size > maxSize) {
        showError(
            "Resume file must be smaller than 10 MB."
        );

        resetFile();
        return;
    }

    currentFile = file;

    fileName.textContent = file.name;
    fileSize.textContent = formatFileSize(file.size);

    selectedFile.classList.remove("hidden");

    uploadArea.style.display = "none";
}


function resetFile() {

    currentFile = null;

    resumeInput.value = "";

    selectedFile.classList.add("hidden");

    uploadArea.style.display = "flex";
}


removeFile.addEventListener("click", () => {
    resetFile();
    hideError();
});


function formatFileSize(bytes) {

    if (bytes < 1024) {
        return `${bytes} B`;
    }

    if (bytes < 1024 * 1024) {
        return `${(bytes / 1024).toFixed(1)} KB`;
    }

    return `${(
        bytes / (1024 * 1024)
    ).toFixed(1)} MB`;
}


/* =========================
   DRAG AND DROP
========================= */

["dragenter", "dragover"].forEach(eventName => {

    uploadArea.addEventListener(
        eventName,
        event => {

            event.preventDefault();

            uploadArea.classList.add(
                "dragging"
            );
        }
    );
});


["dragleave", "drop"].forEach(eventName => {

    uploadArea.addEventListener(
        eventName,
        event => {

            event.preventDefault();

            uploadArea.classList.remove(
                "dragging"
            );
        }
    );
});


uploadArea.addEventListener("drop", event => {

    const files = event.dataTransfer.files;

    if (!files || files.length === 0) {
        return;
    }

    handleFile(files[0]);
});


/* =========================
   JOB DESCRIPTION COUNTER
========================= */

jobDescription.addEventListener("input", () => {

    const length =
        jobDescription.value.length;

    characterCount.textContent =
        `${length.toLocaleString()} characters`;
});


/* =========================
   ANALYZE
========================= */

analyzeButton.addEventListener(
    "click",
    analyzeResume
);


async function analyzeResume() {

    hideError();

    const jd = jobDescription.value.trim();

    if (!currentFile) {
        showError(
            "Please upload your resume before starting the analysis."
        );
        return;
    }

    if (!jd) {
        showError(
            "Please paste the job description before starting the analysis."
        );
        return;
    }

    if (jd.length < 30) {
        showError(
            "The job description is too short. Please provide more details about the role."
        );
        return;
    }

    setLoading(true);

    resultsSection.classList.add("hidden");

    const formData = new FormData();

    formData.append(
        "resume",
        currentFile
    );

    formData.append(
        "jobDescription",
        jd
    );

    try {

        const response = await fetch(
            "/api/analyze",
            {
                method: "POST",
                body: formData
            }
        );

        let data;

        const contentType =
            response.headers.get(
                "content-type"
            );

        if (
            contentType &&
            contentType.includes(
                "application/json"
            )
        ) {

            data = await response.json();

        } else {

            const message =
                await response.text();

            throw new Error(
                message ||
                "The server returned an unexpected response."
            );
        }

        if (!response.ok) {

            throw new Error(
                getServerError(data)
            );
        }

        renderResults(data);

    } catch (error) {

        console.error(
            "Analysis failed:",
            error
        );

        showError(
            error.message ||
            "Unable to analyze the resume. Please try again."
        );

    } finally {

        setLoading(false);
    }
}


/* =========================
   RENDER RESULTS
========================= */

function renderResults(data) {

    if (!data) {
        showError(
            "No analysis result was returned."
        );
        return;
    }

    renderScores(data);

    renderSkills(
        data.skillMatch
    );

    renderRequirements(
        data.requirementMatches
    );

    renderSuggestions(
        data.suggestions
    );

    renderCoverLetter(
        data.coverLetter
    );

    resultsSection.classList.remove(
        "hidden"
    );

    setTimeout(() => {

        resultsSection.scrollIntoView({
            behavior: "smooth",
            block: "start"
        });

    }, 100);
}


/* =========================
   SCORES
========================= */

function renderScores(data) {

    const skill =
        data.skillMatch || {};

    const ats =
        data.atsCompatibility || {};

    const skillPercentage =
        clampPercentage(
            Number(
                skill.skillMatchPercentage || 0
            )
        );

    const atsPercentage =
        clampPercentage(
            Number(
                ats.score || 0
            )
        );


    skillScore.textContent =
        `${formatPercentage(
            skillPercentage
        )}%`;

    skillFraction.textContent =
        `${skill.matchedSkillCount || 0} / ${
            skill.totalRequiredSkills || 0
        } required skills`;


    atsScore.textContent =
        `${formatPercentage(
            atsPercentage
        )}%`;

    atsFraction.textContent =
        `${ats.earnedPoints || 0} / ${
            ats.totalPoints || 0
        } weighted points`;


    skillProgress.style.width = "0%";
    atsProgress.style.width = "0%";


    requestAnimationFrame(() => {

        requestAnimationFrame(() => {

            skillProgress.style.width =
                `${skillPercentage}%`;

            atsProgress.style.width =
                `${atsPercentage}%`;
        });
    });
}


function clampPercentage(value) {

    if (!Number.isFinite(value)) {
        return 0;
    }

    return Math.min(
        100,
        Math.max(0, value)
    );
}


function formatPercentage(value) {

    if (Number.isInteger(value)) {
        return value;
    }

    return value.toFixed(1);
}


/* =========================
   SKILLS
========================= */

function renderSkills(skillMatch) {

    matchedSkills.innerHTML = "";
    missingSkills.innerHTML = "";

    const matched =
        skillMatch?.matchedSkills || [];

    const missing =
        skillMatch?.missingSkills || [];


    if (matched.length === 0) {

        matchedSkills.appendChild(
            createEmptyState(
                "No required skills matched."
            )
        );

    } else {

        matched.forEach(skill => {

            matchedSkills.appendChild(
                createSkillTag(
                    skill,
                    "matched"
                )
            );
        });
    }


    if (missing.length === 0) {

        missingSkills.appendChild(
            createEmptyState(
                "No required skills are missing."
            )
        );

    } else {

        missing.forEach(skill => {

            missingSkills.appendChild(
                createSkillTag(
                    skill,
                    "missing"
                )
            );
        });
    }
}


function createSkillTag(
    skill,
    type
) {

    const element =
        document.createElement("span");

    element.className =
        `skill-tag ${type}`;

    element.textContent = skill;

    return element;
}


function createEmptyState(message) {

    const element =
        document.createElement("span");

    element.className =
        "empty-state";

    element.textContent = message;

    return element;
}


/* =========================
   REQUIREMENTS
========================= */

function renderRequirements(requirements) {

    requirementsList.innerHTML = "";

    if (
        !requirements ||
        requirements.length === 0
    ) {

        requirementsList.appendChild(
            createEmptyState(
                "No additional job requirements were identified."
            )
        );

        return;
    }


    requirements.forEach(requirement => {

        const item =
            document.createElement("div");

        item.className =
            "requirement-item";


        const top =
            document.createElement("div");

        top.className =
            "requirement-top";


        const name =
            document.createElement("div");

        name.className =
            "requirement-name";


        const status =
            document.createElement("span");

        status.className =
            requirement.matched
                ? "requirement-status matched"
                : "requirement-status missing";

        status.textContent =
            requirement.matched
                ? "✓"
                : "×";


        const nameText =
            document.createElement("span");

        nameText.textContent =
            requirement.requirement ||
            "Requirement";


        name.appendChild(status);
        name.appendChild(nameText);


        const priority =
            document.createElement("span");

        priority.className =
            "priority-badge";

        priority.textContent =
            requirement.priority ||
            "REQUIRED";


        top.appendChild(name);
        top.appendChild(priority);


        const evidence =
            document.createElement("div");

        evidence.className =
            "requirement-evidence";


        if (
            requirement.matched &&
            requirement.evidence
        ) {

            const label =
                document.createElement(
                    "strong"
                );

            label.textContent =
                "Evidence: ";

            evidence.appendChild(label);

            evidence.appendChild(
                document.createTextNode(
                    requirement.evidence
                )
            );

        } else {

            evidence.classList.add(
                "no-evidence"
            );

            evidence.textContent =
                "No explicit supporting evidence was found in the resume.";
        }


        item.appendChild(top);
        item.appendChild(evidence);

        requirementsList.appendChild(
            item
        );
    });
}


/* =========================
   SUGGESTIONS
========================= */

function renderSuggestions(suggestions) {

    suggestionsList.innerHTML = "";

    if (
        !suggestions ||
        suggestions.length === 0
    ) {

        suggestionsList.appendChild(
            createEmptyState(
                "No improvement suggestions were generated."
            )
        );

        return;
    }


    suggestions.forEach(
        (suggestion, index) => {

            const item =
                document.createElement(
                    "div"
                );

            item.className =
                "suggestion-item";


            const number =
                document.createElement(
                    "div"
                );

            number.className =
                "suggestion-number";

            number.textContent =
                String(index + 1)
                    .padStart(2, "0");


            const text =
                document.createElement(
                    "div"
                );

            text.className =
                "suggestion-text";

            text.textContent =
                suggestion;


            item.appendChild(number);
            item.appendChild(text);

            suggestionsList.appendChild(
                item
            );
        }
    );
}


/* =========================
   COVER LETTER
========================= */

function renderCoverLetter(letter) {

    if (!letter) {

        coverLetter.textContent =
            "No cover letter was generated.";

        copyButton.disabled = true;

        return;
    }

    coverLetter.textContent = letter;

    copyButton.disabled = false;

    copyButton.textContent =
        "Copy Letter";

    copyButton.classList.remove(
        "copied"
    );
}


copyButton.addEventListener(
    "click",
    async () => {

        const text =
            coverLetter.textContent.trim();

        if (!text) {
            return;
        }

        try {

            await navigator.clipboard.writeText(
                text
            );

            copyButton.textContent =
                "Copied ✓";

            copyButton.classList.add(
                "copied"
            );

            setTimeout(() => {

                copyButton.textContent =
                    "Copy Letter";

                copyButton.classList.remove(
                    "copied"
                );

            }, 2000);

        } catch (error) {

            console.error(
                "Unable to copy:",
                error
            );

            copyButton.textContent =
                "Copy failed";
        }
    }
);


/* =========================
   NEW ANALYSIS
========================= */

newAnalysisButton.addEventListener(
    "click",
    () => {

        resetFile();

        jobDescription.value = "";

        characterCount.textContent =
            "0 characters";

        resultsSection.classList.add(
            "hidden"
        );

        hideError();

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    }
);


/* =========================
   LOADING
========================= */

function setLoading(loading) {

    analyzeButton.disabled = loading;

    if (loading) {

        buttonText.textContent =
            "Analyzing Resume...";

        loadingSpinner.classList.remove(
            "hidden"
        );

        document.querySelector(
            ".button-arrow"
        ).classList.add(
            "hidden"
        );

    } else {

        buttonText.textContent =
            "Analyze Resume";

        loadingSpinner.classList.add(
            "hidden"
        );

        document.querySelector(
            ".button-arrow"
        ).classList.remove(
            "hidden"
        );
    }
}


/* =========================
   ERRORS
========================= */

function showError(message) {

    errorMessage.textContent =
        message;

    errorMessage.classList.remove(
        "hidden"
    );

    errorMessage.scrollIntoView({
        behavior: "smooth",
        block: "center"
    });
}


function hideError() {

    errorMessage.textContent = "";

    errorMessage.classList.add(
        "hidden"
    );
}


function getServerError(data) {

    if (!data) {
        return "Analysis failed.";
    }

    if (typeof data === "string") {
        return data;
    }

    if (data.message) {
        return data.message;
    }

    if (data.detail) {

        if (
            typeof data.detail ===
            "string"
        ) {
            return data.detail;
        }

        return JSON.stringify(
            data.detail
        );
    }

    if (data.error) {
        return data.error;
    }

    return "Unable to analyze the resume. Please try again.";
}