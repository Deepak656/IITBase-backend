package com.iitbase.email.event;

import lombok.Getter;

@Getter
public class RecruiterTeamInviteEvent extends EmailEvent {
    private final String toEmail;
    private final String companyName;
    private final String invitedByName;
    private final String token;

    public RecruiterTeamInviteEvent(Object source, String toEmail,
                                    String companyName, String invitedByName,
                                    String token) {
        super(source);
        this.toEmail       = toEmail;
        this.companyName   = companyName;
        this.invitedByName = invitedByName;
        this.token         = token;
    }
}