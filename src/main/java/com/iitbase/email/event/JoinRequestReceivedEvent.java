package com.iitbase.email.event;

import lombok.Getter;

@Getter
public class JoinRequestReceivedEvent extends EmailEvent {
    private final String adminEmail;
    private final String adminName;
    private final String requesterEmail;
    private final String companyName;
    private final String message;       // optional message from requester

    public JoinRequestReceivedEvent(Object source, String adminEmail,
                                    String adminName, String requesterEmail,
                                    String companyName, String message) {
        super(source);
        this.adminEmail     = adminEmail;
        this.adminName      = adminName;
        this.requesterEmail = requesterEmail;
        this.companyName    = companyName;
        this.message        = message;
    }
}
