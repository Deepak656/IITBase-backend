package com.iitbase.jobseeker.llm;

import com.iitbase.jobseeker.dto.ResumeParseResponseDTO;

/**
 * Contract for LLM-backed resume parsing.
 *
 * Implementations: AnthropicResumeParser, OpenAiResumeParser
 * Swap via @Primary or application.yml flag — no service layer changes needed.
 */
public interface LlmResumeParser {

    /**
     * Parse raw resume text into structured profile data.
     *
     * @param resumeText cleaned plain-text content from the resume
     * @return structured parse result; undetected fields are null, not empty strings
     */
    ResumeParseResponseDTO parse(String resumeText);
}