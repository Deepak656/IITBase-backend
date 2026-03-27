package com.iitbase.application.event;

import com.iitbase.application.enums.ApplicationStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ApplicationStatusChangedEvent extends ApplicationEvent {

    private final Long applicationId;
    private final Long jobseekerId;
    private final Long recruiterId;
    private final Long jobId;
    private final String jobTitle;
    private final ApplicationStatus previousStatus;
    private final ApplicationStatus newStatus;

    public ApplicationStatusChangedEvent(
            Object source,
            Long applicationId,
            Long jobseekerId,
            Long recruiterId,
            Long jobId,
            String jobTitle,
            ApplicationStatus previousStatus,
            ApplicationStatus newStatus) {
        super(source);
        this.applicationId = applicationId;
        this.jobseekerId   = jobseekerId;
        this.recruiterId   = recruiterId;
        this.jobId         = jobId;
        this.jobTitle      = jobTitle;
        this.previousStatus = previousStatus;
        this.newStatus      = newStatus;
    }
}