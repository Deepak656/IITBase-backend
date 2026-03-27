package com.iitbase.recruiter.exception;

public class RecruiterNotFoundException extends RuntimeException {
    public RecruiterNotFoundException(Long userId) {
        super("Recruiter profile not found for userId: " + userId);
    }
}
