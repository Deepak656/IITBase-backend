package com.iitbase.jobseeker.llm;

/**
 * Thrown when resume parsing fails — either at the LLM call layer
 * or during JSON deserialization of the response.
 *
 * Maps to 422 Unprocessable Entity via GlobalExceptionHandler.
 */
public class ResumeParseException extends RuntimeException {

    public ResumeParseException(String message) {
        super(message);
    }

    public ResumeParseException(String message, Throwable cause) {
        super(message, cause);
    }
}