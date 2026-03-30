package com.iitbase.jobseeker.service;

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
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Orchestrates the full resume parse pipeline:
 *   1. Resolve jobseeker → get resumeUrl
 *   2. Fetch PDF bytes from Cloudflare R2 (public URL — no auth needed)
 *   3. Extract plain text via PDFBox / POI
 *   4. Send to LLM, get structured JSON back
 *   5. Return parsed DTO to controller
 *
 * This service does NOT save anything. The frontend pre-fills the form
 * and the user confirms — existing profile save endpoints handle persistence.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeParseService {

    private final JobseekerProfileService profileService;
    private final ResumeTextExtractor textExtractor;
    private final LlmResumeParser llmResumeParser;
    private final WebClient.Builder webClientBuilder;

    @Transactional(readOnly = true)
    public ResumeParseResponseDTO parseUploadedResume(String email) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);

        if (jobseeker.getResumeUrl() == null || jobseeker.getResumeUrl().isBlank()) {
            throw new ResourceNotFoundException("No resume uploaded. Please upload a resume first.");
        }

        String resumeUrl = jobseeker.getResumeUrl();
        log.info("Starting resume parse for jobseeker={} url={}", jobseeker.getId(), resumeUrl);

        // Step 1 — fetch raw bytes from R2 public URL
        byte[] fileBytes = fetchFromR2(resumeUrl);

        // Step 2 — detect content type from URL (reliable since we control the upload path)
        String contentType = detectContentType(resumeUrl);

        // Step 3 — extract text
        String resumeText;
        try (InputStream stream = new ByteArrayInputStream(fileBytes)) {
            resumeText = textExtractor.extract(stream, contentType);
        } catch (Exception e) {
            log.error("Text extraction failed for jobseeker={}", jobseeker.getId(), e);
            throw new ResumeParseException("Could not read the resume file. Make sure it's a valid PDF or DOCX.");
        }

        if (resumeText.isBlank()) {
            throw new ResumeParseException(
                    "The resume appears to be empty or image-based (scanned PDF). " +
                            "Please upload a text-based PDF or DOCX."
            );
        }

        // Step 4 — LLM parse
        log.debug("Sending {} chars to LLM for jobseeker={}", resumeText.length(), jobseeker.getId());
        ResumeParseResponseDTO result = llmResumeParser.parse(resumeText);

        log.info("Resume parse complete for jobseeker={} — found {} experiences, {} skills",
                jobseeker.getId(),
                result.getWorkExperiences() != null ? result.getWorkExperiences().size() : 0,
                result.getSkills() != null ? result.getSkills().size() : 0
        );

        return result;
    }

    // ─────────────────────────────────────────────
    // Fetch resume bytes from public R2 URL
    // ─────────────────────────────────────────────

    private byte[] fetchFromR2(String url) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch resume from R2: {}", url, e);
            throw new ResumeParseException("Could not retrieve the resume file. Please try again.");
        }
    }

    // ─────────────────────────────────────────────
    // Infer content type from the R2 key path
    // R2StorageService always stores resumes as .pdf
    // ─────────────────────────────────────────────

    private String detectContentType(String url) {
        String lower = url.toLowerCase();
        if (lower.endsWith(".pdf"))  return MediaType.APPLICATION_PDF_VALUE;
        if (lower.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (lower.endsWith(".doc"))  return "application/msword";
        // R2StorageService enforces PDF-only for resumes — this is a safe default
        return MediaType.APPLICATION_PDF_VALUE;
    }
}