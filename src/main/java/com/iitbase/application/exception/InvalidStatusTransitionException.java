package com.iitbase.application.exception;

import com.iitbase.application.enums.ApplicationStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(
            ApplicationStatus from, ApplicationStatus to) {
        super("Invalid transition: " + from + " → " + to);
    }
    public InvalidStatusTransitionException(
            ApplicationStatus from, ApplicationStatus to, String reason) {
        super("Invalid transition: " + from + " → " + to + ". " + reason);
    }
}
