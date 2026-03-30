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

import java.util.List;
import java.util.Map;

/**
 * OpenAI GPT-4o-mini implementation of LlmResumeParser.
 *
 * Activate by setting: llm.provider=openai
 * Uses gpt-4o-mini — cheaper than gpt-4o, still reliable for structured extraction.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "llm.provider", havingValue = "openai")
@RequiredArgsConstructor
public class OpenAiResumeParser implements LlmResumeParser {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL          = "gpt-4o-mini";
    private static final int    MAX_TOKENS     = 4096;

    @Value("${openai.api-key}")
    private String apiKey;

    private final ObjectMapper objectMapper;
    private final WebClient.Builder webClientBuilder;

    @Override
    public ResumeParseResponseDTO parse(String resumeText) {
        String prompt = ResumeParsePrompt.build(resumeText);
        String rawJson = callApi(prompt);
        // Reuse the same deserializer — JSON schema is provider-agnostic
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
            String response = webClientBuilder.build()
                    .post()
                    .uri(OPENAI_API_URL)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            return root.at("/choices/0/message/content").asText();

        } catch (Exception e) {
            log.error("OpenAI API call failed during resume parse", e);
            throw new ResumeParseException("Failed to reach the AI parsing service. Please try again.");
        }
    }

    private ResumeParseResponseDTO deserialize(String rawJson) {
        // Identical deserialization logic — extracted to a shared utility if both parsers
        // are ever active simultaneously, but @ConditionalOnProperty ensures only one is.
        try {
            String clean = rawJson
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();

            // Delegate to Jackson — same schema as AnthropicResumeParser
            return objectMapper.readValue(clean, ResumeParseResponseDTO.class);

        } catch (Exception e) {
            log.error("Failed to deserialize OpenAI response: {}", rawJson, e);
            throw new ResumeParseException("Resume parsing produced an unexpected response. Please try again.");
        }
    }
}