package com.iitbase.email.event;

import lombok.Getter;

@Getter
public class JoinRequestApprovedEvent extends EmailEvent {
    private final String requesterEmail;
    private final String companyName;
    private final String approvedByName;

    public JoinRequestApprovedEvent(Object source, String requesterEmail,
                                    String companyName, String approvedByName) {
        super(source);
        this.requesterEmail = requesterEmail;
        this.companyName    = companyName;
        this.approvedByName = approvedByName;
    }
}