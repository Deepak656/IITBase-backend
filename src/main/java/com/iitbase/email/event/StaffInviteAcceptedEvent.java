package com.iitbase.email.event;

import lombok.Getter;

@Getter
public class StaffInviteAcceptedEvent extends EmailEvent {
    private final String senderEmail;      // the admin who sent the invite
    private final String acceptedByEmail;  // who accepted

    public StaffInviteAcceptedEvent(Object source, String senderEmail,
                                    String acceptedByEmail) {
        super(source);
        this.senderEmail     = senderEmail;
        this.acceptedByEmail = acceptedByEmail;
    }
}