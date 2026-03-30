package com.iitbase.jobseeker.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iitbase.jobseeker.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Resume parser backed by Anthropic's Claude API.
 *
 * Active when: llm.provider=anthropic (default)
 * Swap to OpenAI by setting llm.provider=openai and adding OpenAiResumeParser.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "llm.provider", havingValue = "anthropic", matchIfMissing = true)
@RequiredArgsConstructor
public class AnthropicResumeParser implements LlmResumeParser {

    private static final String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL             = "claude-haiku-4-5-20251001"; // fast + cheap for parsing
    private static final int    MAX_TOKENS        = 4096;

    @Value("${anthropic.api-key}")
    private String apiKey;

    private final ObjectMapper objectMapper;
    private final WebClient.Builder webClientBuilder;

    @Override
    public ResumeParseResponseDTO parse(String resumeText) {
        String prompt = ResumeParsePrompt.build(resumeText);
        String rawJson = callApi(prompt);
        return deserialize(rawJson);
    }

    // ─────────────────────────────────────────────
    // API call
    // ─────────────────────────────────────────────

    private String callApi(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", MODEL,
                "max_tokens", MAX_TOKENS,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        try {
            String response = webClientBuilder.build()
                    .post()
                    .uri(ANTHROPIC_API_URL)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            return root.at("/content/0/text").asText();

        } catch (Exception e) {
            log.error("Anthropic API call failed during resume parse", e);
            throw new ResumeParseException("Failed to reach the AI parsing service. Please try again.");
        }
    }

    // ─────────────────────────────────────────────
    // Deserialize LLM response → DTO
    // ─────────────────────────────────────────────

    private ResumeParseResponseDTO deserialize(String rawJson) {
        try {
            // Strip markdown fences if the model hallucinated them despite the prompt
            String clean = rawJson
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();

            JsonNode root = objectMapper.readTree(clean);

            return ResumeParseResponseDTO.builder()
                    .basicInfo(parseBasicInfo(root.path("basicInfo")))
                    .workExperiences(parseWorkExperiences(root.path("workExperiences")))
                    .educations(parseEducations(root.path("educations")))
                    .skills(parseSkills(root.path("skills")))
                    .projects(parseProjects(root.path("projects")))
                    .certifications(parseCertifications(root.path("certifications")))
                    .jobPreference(parseJobPreference(root.path("jobPreference")))
                    .build();

        } catch (Exception e) {
            log.error("Failed to deserialize LLM response: {}", rawJson, e);
            throw new ResumeParseException("Resume parsing produced an unexpected response. Please try again.");
        }
    }

    private ResumeParseResponseDTO.BasicInfo parseBasicInfo(JsonNode node) {
        if (node.isMissingNode()) return null;
        return ResumeParseResponseDTO.BasicInfo.builder()
                .fullName(text(node, "fullName"))
                .phone(text(node, "phone"))
                .headline(text(node, "headline"))
                .summary(text(node, "summary"))
                .linkedinUrl(text(node, "linkedinUrl"))
                .githubUrl(text(node, "githubUrl"))
                .portfolioUrl(text(node, "portfolioUrl"))
                .yearsOfExperience(decimal(node, "yearsOfExperience"))
                .build();
    }

    private List<WorkExperienceDTO> parseWorkExperiences(JsonNode array) {
        List<WorkExperienceDTO> list = new ArrayList<>();
        if (!array.isArray()) return list;
        for (JsonNode n : array) {
            list.add(WorkExperienceDTO.builder()
                    .company(text(n, "company"))
                    .title(text(n, "title"))
                    .location(text(n, "location"))
                    .employmentType(text(n, "employmentType"))
                    .startMonth(integer(n, "startMonth"))
                    .startYear(integer(n, "startYear"))
                    .endMonth(integer(n, "endMonth"))
                    .endYear(integer(n, "endYear"))
                    .isCurrent(bool(n, "isCurrent"))
                    .description(text(n, "description"))
                    .skillsUsed(text(n, "skillsUsed"))
                    .displayOrder(integer(n, "displayOrder"))
                    .build());
        }
        return list;
    }

    private List<EducationDTO> parseEducations(JsonNode array) {
        List<EducationDTO> list = new ArrayList<>();
        if (!array.isArray()) return list;
        for (JsonNode n : array) {
            list.add(EducationDTO.builder()
                    .institution(text(n, "institution"))
                    .degree(text(n, "degree"))
                    .fieldOfStudy(text(n, "fieldOfStudy"))
                    .startYear(integer(n, "startYear"))
                    .endYear(integer(n, "endYear"))
                    .grade(text(n, "grade"))
                    .gradeType(text(n, "gradeType"))
                    .description(text(n, "description"))
                    .displayOrder(integer(n, "displayOrder"))
                    .build());
        }
        return list;
    }

    private List<SkillDTO> parseSkills(JsonNode array) {
        List<SkillDTO> list = new ArrayList<>();
        if (!array.isArray()) return list;
        for (JsonNode n : array) {
            list.add(SkillDTO.builder()
                    .name(text(n, "name"))
                    .proficiencyLevel(text(n, "proficiencyLevel"))
                    .yearsOfExperience(integer(n, "yearsOfExperience"))
                    .displayOrder(integer(n, "displayOrder"))
                    .build());
        }
        return list;
    }

    private List<ProjectDTO> parseProjects(JsonNode array) {
        List<ProjectDTO> list = new ArrayList<>();
        if (!array.isArray()) return list;
        for (JsonNode n : array) {
            list.add(ProjectDTO.builder()
                    .title(text(n, "title"))
                    .description(text(n, "description"))
                    .techStack(text(n, "techStack"))
                    .projectUrl(text(n, "projectUrl"))
                    .repoUrl(text(n, "repoUrl"))
                    .startMonth(integer(n, "startMonth"))
                    .startYear(integer(n, "startYear"))
                    .endMonth(integer(n, "endMonth"))
                    .endYear(integer(n, "endYear"))
                    .isOngoing(bool(n, "isOngoing"))
                    .displayOrder(integer(n, "displayOrder"))
                    .build());
        }
        return list;
    }

    private List<CertificationDTO> parseCertifications(JsonNode array) {
        List<CertificationDTO> list = new ArrayList<>();
        if (!array.isArray()) return list;
        for (JsonNode n : array) {
            list.add(CertificationDTO.builder()
                    .name(text(n, "name"))
                    .issuer(text(n, "issuer"))
                    .issueMonth(integer(n, "issueMonth"))
                    .issueYear(integer(n, "issueYear"))
                    .expiryMonth(integer(n, "expiryMonth"))
                    .expiryYear(integer(n, "expiryYear"))
                    .doesNotExpire(bool(n, "doesNotExpire"))
                    .credentialId(text(n, "credentialId"))
                    .credentialUrl(text(n, "credentialUrl"))
                    .displayOrder(integer(n, "displayOrder"))
                    .build());
        }
        return list;
    }
    private ResumeParseResponseDTO.ParsedJobPreference parseJobPreference(JsonNode node) {
        if (node.isMissingNode() || node.isNull()) return null;
        return ResumeParseResponseDTO.ParsedJobPreference.builder()
                .currentLocation(text(node, "currentLocation"))
                .primaryRole(text(node, "primaryRole"))
                .build();
    }

    // ─────────────────────────────────────────────
    // Safe node readers — null instead of exception
    // ─────────────────────────────────────────────

    private String text(JsonNode node, String field) {
        JsonNode n = node.path(field);
        return (n.isNull() || n.isMissingNode()) ? null : n.asText().isBlank() ? null : n.asText().trim();
    }

    private Integer integer(JsonNode node, String field) {
        JsonNode n = node.path(field);
        return (n.isNull() || n.isMissingNode()) ? null : n.asInt();
    }

    private Double decimal(JsonNode node, String field) {
        JsonNode n = node.path(field);
        return (n.isNull() || n.isMissingNode()) ? null : n.asDouble();
    }

    private Boolean bool(JsonNode node, String field) {
        JsonNode n = node.path(field);
        return (n.isNull() || n.isMissingNode()) ? false : n.asBoolean();
    }
}