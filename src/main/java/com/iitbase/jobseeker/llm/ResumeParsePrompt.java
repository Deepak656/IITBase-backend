package com.iitbase.jobseeker.llm;

/**
 * Builds the LLM prompt for resume parsing.
 *
 * Kept in its own class because prompt iteration is frequent —
 * you don't want it buried inside a service method.
 *
 * The schema in the prompt must stay in sync with ResumeParseResponseDTO.
 */
public final class ResumeParsePrompt {

    private ResumeParsePrompt() {}

    public static String build(String resumeText) {
        return """
                Extract structured data from the resume text below.
                Return ONLY a valid JSON object. No explanation. No markdown fences. No extra keys.

                JSON schema (follow exactly — unknown fields will cause a parse failure):
                {
                  "basicInfo": {
                    "fullName": "string or null",
                    "phone": "string or null",
                    "headline": "string — infer from most recent role, e.g. 'Backend Engineer | IIT Bombay | 3 yrs', or null",
                    "summary": "string — professional summary if present, else null",
                    "linkedinUrl": "string or null",
                    "githubUrl": "string or null",
                    "portfolioUrl": "string or null",
                    "yearsOfExperience": "number or null — sum of all work experience durations, rounded to 1 decimal"
                  },
                  "workExperiences": [
                    {
                      "company": "string",
                      "title": "string",
                      "location": "string or null",
                      "employmentType": "one of FULL_TIME | PART_TIME | CONTRACT | INTERNSHIP | FREELANCE or null",
                      "startMonth": "integer 1-12 or null",
                      "startYear": "integer or null",
                      "endMonth": "integer 1-12 or null",
                      "endYear": "integer or null",
                      "isCurrent": "boolean",
                      "description": "string or null",
                      "skillsUsed": "comma-separated string of tech skills mentioned, or null",
                      "displayOrder": "integer starting from 0, most recent first"
                    }
                  ],
                  "educations": [
                    {
                      "institution": "string",
                      "degree": "string — e.g. B.Tech, M.Tech, MBA",
                      "fieldOfStudy": "string or null",
                      "startYear": "integer or null",
                      "endYear": "integer or null",
                      "grade": "string or null — preserve original format (8.7, 91%%, etc.)",
                      "gradeType": "one of CGPA | PERCENTAGE | GPA or null",
                      "description": "string or null",
                      "displayOrder": "integer starting from 0"
                    }
                  ],
                  "skills": [
                    {
                      "name": "string — individual skill, not a category",
                      "proficiencyLevel": "one of BEGINNER | INTERMEDIATE | ADVANCED | EXPERT or null",
                      "yearsOfExperience": "integer or null",
                      "displayOrder": "integer starting from 0"
                    }
                  ],
                  "projects": [
                    {
                      "title": "string",
                      "description": "string or null",
                      "techStack": "comma-separated tech used, or null",
                      "projectUrl": "string or null",
                      "repoUrl": "string or null",
                      "startMonth": "integer 1-12 or null",
                      "startYear": "integer or null",
                      "endMonth": "integer 1-12 or null",
                      "endYear": "integer or null",
                      "isOngoing": "boolean",
                      "displayOrder": "integer starting from 0"
                    }
                  ],
                  "certifications": [
                    {
                      "name": "string",
                      "issuer": "string",
                      "issueMonth": "integer 1-12 or null",
                      "issueYear": "integer or null",
                      "expiryMonth": "integer 1-12 or null",
                      "expiryYear": "integer or null",
                      "doesNotExpire": "boolean",
                      "credentialId": "string or null",
                      "credentialUrl": "string or null",
                      "displayOrder": "integer starting from 0"
                    }
                  ],
                  "jobPreference": {
                    "currentLocation": "string — candidate's current city/country e.g. 'Bengaluru, India', or null",
                    "primaryRole": "string — their primary job function inferred from most recent role e.g. 'Backend Engineer', 'Full Stack Developer', 'Data Engineer', or null"
                  }
                }

                Rules:
                - If a section has no entries, return an empty array [].
                - Never invent data. If something isn't in the resume, return null.
                - Skills must be individual items (Java, not "Programming Languages").
                - For isCurrent / isOngoing: true only if the resume explicitly says "Present" or "Ongoing".
                - Infer employmentType only if clearly stated or strongly implied (e.g. "Intern" → INTERNSHIP).
                - gradeType: CGPA if value is <= 10, PERCENTAGE if value ends with %%, GPA if scale is mentioned.
                - Return all work experiences, educations, projects, certifications found — do not truncate.
                - jobPreference.currentLocation: look in the resume header/contact section for city, state, country.
                - jobPreference.primaryRole: infer from the most recent job title, keep it concise (2-3 words max).
                Resume text:
                ---
                %s
                ---
                """.formatted(resumeText);
    }
}