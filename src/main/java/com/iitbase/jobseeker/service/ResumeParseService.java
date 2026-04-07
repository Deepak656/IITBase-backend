package com.iitbase.jobseeker.service;

import com.iitbase.common.MemoryLogger;
import com.iitbase.exception.ResourceNotFoundException;
import com.iitbase.jobseeker.dto.ResumeParseResponseDTO;
import com.iitbase.jobseeker.extractor.ResumeTextExtractor;
import com.iitbase.jobseeker.llm.LlmResumeParser;
import com.iitbase.jobseeker.llm.ResumeParseException;
import com.iitbase.jobseeker.model.Jobseeker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Resume Parse Pipeline:
 * 1. Resolve jobseeker
 * 2. Stream resume directly from R2 (no byte[] load)
 * 3. Extract text
 * 4. Send to LLM
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeParseService {

    private final JobseekerProfileService profileService;
    private final ResumeTextExtractor textExtractor;
    private final LlmResumeParser llmResumeParser;

    private final RestTemplate restTemplate = new RestTemplate(); // lightweight

    @Transactional(readOnly = true)
    public ResumeParseResponseDTO parseUploadedResume(String email) {

        Jobseeker jobseeker = profileService.resolveJobseeker(email);

        if (jobseeker.getResumeUrl() == null || jobseeker.getResumeUrl().isBlank()) {
            throw new ResourceNotFoundException("No resume uploaded. Please upload a resume first.");
        }

        String resumeUrl = jobseeker.getResumeUrl();

        log.info("Starting resume parse for jobseeker={} url={}", jobseeker.getId(), resumeUrl);
        MemoryLogger.log("START_PARSE");

        String contentType = detectContentType(resumeUrl);

        String resumeText;

        try (InputStream stream = fetchStreamFromR2(resumeUrl)) {

            MemoryLogger.log("BEFORE_TEXT_EXTRACTION");

            resumeText = textExtractor.extract(stream, contentType);

            MemoryLogger.log("AFTER_TEXT_EXTRACTION");

        } catch (Exception e) {
            log.error("Text extraction failed for jobseeker={}", jobseeker.getId(), e);
            throw new ResumeParseException("Could not read the resume file. Make sure it's a valid PDF.");
        }

        if (resumeText.isBlank()) {
            throw new ResumeParseException(
                    "The resume appears to be empty or image-based (scanned PDF). " +
                            "Please upload a text-based PDF."
            );
        }

        log.debug("Sending {} chars to LLM for jobseeker={}", resumeText.length(), jobseeker.getId());

        ResumeParseResponseDTO result = llmResumeParser.parse(resumeText);

        log.info("Resume parse complete for jobseeker={} — found {} experiences, {} skills",
                jobseeker.getId(),
                result.getWorkExperiences() != null ? result.getWorkExperiences().size() : 0,
                result.getSkills() != null ? result.getSkills().size() : 0
        );

        MemoryLogger.log("AFTER_PARSE");

        return result;
    }

    // ─────────────────────────────────────────────
    // STREAMING FETCH (No byte[] → huge memory win)
    // ─────────────────────────────────────────────

    private InputStream fetchStreamFromR2(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000);

            return connection.getInputStream(); // 🔥 streaming, no heap spike

        } catch (Exception e) {
            log.error("Failed to fetch resume from R2: {}", url, e);
            throw new ResumeParseException("Could not retrieve the resume file. Please try again.");
        }
    }

    // ─────────────────────────────────────────────
    // Content type detection
    // ─────────────────────────────────────────────

    private String detectContentType(String url) {
        String lower = url.toLowerCase();

        if (lower.endsWith(".pdf"))  return MediaType.APPLICATION_PDF_VALUE;
        if (lower.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (lower.endsWith(".doc"))  return "application/msword";

        return MediaType.APPLICATION_PDF_VALUE;
    }
}