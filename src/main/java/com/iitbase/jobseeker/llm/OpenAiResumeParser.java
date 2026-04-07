package com.iitbase.jobseeker.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iitbase.jobseeker.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "llm.provider", havingValue = "openai", matchIfMissing = true)
@RequiredArgsConstructor
public class OpenAiResumeParser implements LlmResumeParser {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o-mini";
    private static final int MAX_TOKENS = 4096;

    @Value("${openai.api-key}")
    private String apiKey;

    private final ObjectMapper objectMapper;

    // Reuse single instance (better than creating per request)
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public ResumeParseResponseDTO parse(String resumeText) {
        String prompt = ResumeParsePrompt.build(resumeText);
        String rawJson = callApi(prompt);
        return deserialize(rawJson);
    }

    private String callApi(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", MODEL,
                "max_tokens", MAX_TOKENS,
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "You are a resume parser. Return only valid JSON. No markdown, no explanation."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    OPENAI_API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String body = response.getBody();

            if (body == null || body.isBlank()) {
                throw new ResumeParseException("Empty response from AI service");
            }

            JsonNode root = objectMapper.readTree(body);
            return root.at("/choices/0/message/content").asText();

        } catch (Exception e) {
            log.error("OpenAI API call failed during resume parse", e);
            throw new ResumeParseException("Failed to reach the AI parsing service. Please try again.");
        }
    }

    private ResumeParseResponseDTO deserialize(String rawJson) {
        try {
            String clean = rawJson
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();

            return objectMapper.readValue(clean, ResumeParseResponseDTO.class);

        } catch (Exception e) {
            log.error("Failed to deserialize OpenAI response: {}", rawJson, e);
            throw new ResumeParseException("Resume parsing produced an unexpected response. Please try again.");
        }
    }
}