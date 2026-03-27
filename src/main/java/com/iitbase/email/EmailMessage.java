package com.iitbase.email;

import lombok.Builder;
import lombok.Getter;

/**
 * Provider-agnostic email payload.
 * Build once, hand off to whatever EmailProvider is currently wired in.
 */
@Getter
@Builder
public class EmailMessage {

    private final String to;
    private final String subject;
    private final String htmlBody;
    private final String textBody; // fallback plain-text; optional but good for spam scores
}