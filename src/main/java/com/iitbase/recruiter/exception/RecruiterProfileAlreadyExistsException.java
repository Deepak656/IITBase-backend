package com.iitbase.recruiter.exception;

public class RecruiterProfileAlreadyExistsException extends RuntimeException {
    public RecruiterProfileAlreadyExistsException() {
        super("Recruiter profile already exists for this user");
    }
}
