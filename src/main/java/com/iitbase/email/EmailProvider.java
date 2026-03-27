package com.iitbase.email;

/**
 * Strategy interface for email delivery.
 * Swap providers without touching business logic — just bind a different implementation.
 */
public interface EmailProvider {

    void send(EmailMessage message);
}