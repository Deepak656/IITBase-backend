package com.iitbase.email.event;

import lombok.Getter;

@Getter
public class RecruiterCompanyVerifiedEvent extends EmailEvent {

    // All admins of the company get this email
    private final String recruiterEmail;
    private final String recruiterName;
    private final String companyName;

    public RecruiterCompanyVerifiedEvent(Object source,
                                         String recruiterEmail,
                                         String recruiterName,
                                         String companyName) {
        super(source);
        this.recruiterEmail = recruiterEmail;
        this.recruiterName  = recruiterName;
        this.companyName    = companyName;
    }
}