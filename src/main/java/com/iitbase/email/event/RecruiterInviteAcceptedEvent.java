package com.iitbase.email.event;

import lombok.Getter;

@Getter
public class RecruiterInviteAcceptedEvent extends EmailEvent {
    private final String adminEmail;   // who to notify
    private final String adminName;
    private final String newMemberName;
    private final String companyName;

    public RecruiterInviteAcceptedEvent(Object source, String adminEmail,
                                        String adminName, String newMemberName,
                                        String companyName) {
        super(source);
        this.adminEmail    = adminEmail;
        this.adminName     = adminName;
        this.newMemberName = newMemberName;
        this.companyName   = companyName;
    }
}