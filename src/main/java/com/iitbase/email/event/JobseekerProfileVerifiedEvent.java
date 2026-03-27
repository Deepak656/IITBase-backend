package com.iitbase.email.event;

import lombok.Getter;

@Getter
public class JobseekerProfileVerifiedEvent extends EmailEvent {

    private final String email;
    private final String fullName;

    public JobseekerProfileVerifiedEvent(Object source, String email, String fullName) {
        super(source);
        this.email    = email;
        this.fullName = fullName;
    }
}