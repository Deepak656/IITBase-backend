package com.iitbase.application.exception;

public class DuplicateApplicationException extends RuntimeException {
    public DuplicateApplicationException(Long jobId, Long jobseekerId) {
        super("You have already applied to this job");
    }
}
