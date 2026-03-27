package com.iitbase.notification.dto.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ProfileViewedEvent extends ApplicationEvent {
    private final Long jobseekerId;
    private final Long recruiterId;
    private final String companyName;

    public ProfileViewedEvent(Object source, Long jobseekerId,
                              Long recruiterId, String companyName) {
        super(source);
        this.jobseekerId = jobseekerId;
        this.recruiterId = recruiterId;
        this.companyName = companyName;
    }
}