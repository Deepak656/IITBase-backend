package com.iitbase.email.event;

import lombok.Getter;

@Getter
public class JoinRequestRejectedEvent extends EmailEvent {
    private final String requesterEmail;
    private final String companyName;
    private final String rejectionReason;  // nullable

    public JoinRequestRejectedEvent(Object source, String requesterEmail,
                                    String companyName, String rejectionReason) {
        super(source);
        this.requesterEmail  = requesterEmail;
        this.companyName     = companyName;
        this.rejectionReason = rejectionReason;
    }
}