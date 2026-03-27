package com.iitbase.email.event;

import lombok.Getter;

@Getter
public class StaffInviteEvent extends EmailEvent {
    private final String toEmail;
    private final String invitedByEmail;
    private final String token;

    public StaffInviteEvent(Object source, String toEmail,
                            String invitedByEmail, String token) {
        super(source);
        this.toEmail       = toEmail;
        this.invitedByEmail = invitedByEmail;
        this.token         = token;
    }
}