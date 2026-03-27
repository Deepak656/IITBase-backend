package com.iitbase.notification.dto.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewApplicationEvent extends ApplicationEvent {
    private final Long applicationId;
    private final Long recruiterId;
    private final Long jobseekerId;
    private final Long jobId;
    private final String jobTitle;
    private final String jobseekerName;

    public NewApplicationEvent(Object source, Long applicationId,
                               Long recruiterId, Long jobseekerId,
                               Long jobId, String jobTitle,
                               String jobseekerName) {
        super(source);
        this.applicationId = applicationId;
        this.recruiterId   = recruiterId;
        this.jobseekerId   = jobseekerId;
        this.jobId         = jobId;
        this.jobTitle      = jobTitle;
        this.jobseekerName = jobseekerName;
    }
}