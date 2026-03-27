package com.iitbase.recruiter.exception;

public class RecruiterJobNotFoundException extends RuntimeException {
    public RecruiterJobNotFoundException(Long id) {
        super("Recruiter job not found with id: " + id);
    }
}
