package com.iitbase.notification.dto.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class JobInviteEvent extends ApplicationEvent {
    private final Long inviteId;
    private final Long jobseekerId;
    private final Long recruiterId;
    private final Long jobId;
    private final String jobTitle;
    private final String companyName;
    private final String message;

    public JobInviteEvent(Object source, Long inviteId, Long jobseekerId,
                          Long recruiterId, Long jobId, String jobTitle,
                          String companyName, String message) {
        super(source);
        this.inviteId    = inviteId;
        this.jobseekerId = jobseekerId;
        this.recruiterId = recruiterId;
        this.jobId       = jobId;
        this.jobTitle    = jobTitle;
        this.companyName = companyName;
        this.message     = message;
    }
}